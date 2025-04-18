/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.nashorn.tools;

import org.openjdk.nashorn.api.scripting.NashornException;
import org.openjdk.nashorn.internal.codegen.Compiler;
import org.openjdk.nashorn.internal.codegen.Compiler.CompilationPhases;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.debug.ASTWriter;
import org.openjdk.nashorn.internal.ir.debug.PrintVisitor;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.parser.Parser;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ErrorManager;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.ScriptEnvironment;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Symbol;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import org.openjdk.nashorn.internal.runtime.options.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.openjdk.nashorn.internal.runtime.Source.sourceFor;

/**
 * Command line Shell for processing JavaScript files.
 */
public class Shell implements PartialParser {

    /**
     * Resource name for properties file
     */
    private static final String MESSAGE_RESOURCE = "org.openjdk.nashorn.tools.resources.Shell";
    /**
     * Shell message bundle.
     */
    protected static final ResourceBundle bundle = ResourceBundle.getBundle(MESSAGE_RESOURCE, Locale.getDefault());

    /**
     * Exit code for command line tool - successful
     */
    public static final int SUCCESS = 0;
    /**
     * Exit code for command line tool - error on command line
     */
    public static final int COMMANDLINE_ERROR = 100;
    /**
     * Exit code for command line tool - error compiling script
     */
    public static final int COMPILATION_ERROR = 101;
    /**
     * Exit code for command line tool - error during runtime
     */
    public static final int RUNTIME_ERROR = 102;
    /**
     * Exit code for command line tool - i/o error
     */
    public static final int IO_ERROR = 103;
    /**
     * Exit code for command line tool - internal error
     */
    public static final int INTERNAL_ERROR = 104;

    /**
     * Constructor
     */
    protected Shell() {
    }

    /**
     * Main entry point with the default input, output and error streams.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        try {
            final int exitCode = main(System.in, System.out, System.err, args);
            if (exitCode != SUCCESS) {
                System.exit(exitCode);
            }
        } catch (final IOException e) {
            System.err.println(e); //bootstrapping, Context.err may not exist
            System.exit(IO_ERROR);
        }
    }

    /**
     * Starting point for executing a {@code Shell}. Starts a shell with the
     * given arguments and streams and lets it run until exit.
     *
     * @param in input stream for Shell
     * @param out output stream for Shell
     * @param err error stream for Shell
     * @param args arguments to Shell
     *
     * @return exit code
     *
     * @throws IOException if there's a problem setting up the streams
     */
    public static int main(final InputStream in, final OutputStream out, final OutputStream err, final String[] args) throws IOException {
        return new Shell().run(in, out, err, args);
    }

    /**
     * Run method logic.
     *
     * @param in input stream for Shell
     * @param out output stream for Shell
     * @param err error stream for Shell
     * @param args arguments to Shell
     *
     * @return exit code
     *
     * @throws IOException if there's a problem setting up the streams
     */
    protected final int run(final InputStream in, final OutputStream out, final OutputStream err, final String[] args) throws IOException {
        final Context context = makeContext(in, out, err, args);
        if (context == null) {
            return COMMANDLINE_ERROR;
        }

        final Global global = context.createGlobal();
        final ScriptEnvironment env = context.getEnv();
        final List<String> files = env.getFiles();
        if (files.isEmpty()) {
            return readEvalPrint(context, global);
        }

        if (env._compile_only) {
            return compileScripts(context, global, files);
        }

        if (env._fx) {
            return runFXScripts(context, global, files);
        }

        return runScripts(context, global, files);
    }

    /**
     * Make a new Nashorn Context to compile and/or run JavaScript files.
     *
     * @param in input stream for Shell
     * @param out output stream for Shell
     * @param err error stream for Shell
     * @param args arguments to Shell
     *
     * @return null if there are problems with option parsing.
     */
    private static Context makeContext(final InputStream in, final OutputStream out, final OutputStream err, final String[] args) {
        final PrintStream pout = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        final PrintStream perr = err instanceof PrintStream ? (PrintStream) err : new PrintStream(err);
        final PrintWriter wout = new PrintWriter(pout, true);
        final PrintWriter werr = new PrintWriter(perr, true);

        // Set up error handler.
        final ErrorManager errors = new ErrorManager(werr);
        // Set up options.
        final Options options = new Options("nashorn", werr);

        // parse options
        if (args != null) {
            try {
                final String[] prepArgs = preprocessArgs(args);
                options.process(prepArgs);
            } catch (final IllegalArgumentException e) {
                werr.println(bundle.getString("shell.usage"));
                options.displayHelp(e);
                return null;
            }
        }

        // detect scripting mode by any source's first character being '#'
        if (!options.getBoolean("scripting")) {
            for (final String fileName : options.getFiles()) {
                final File firstFile = new File(fileName);
                if (firstFile.isFile()) {
                    try (final FileReader fr = new FileReader(firstFile)) {
                        final int firstChar = fr.read();
                        // starts with '#
                        if (firstChar == '#') {
                            options.set("scripting", true);
                            break;
                        }
                    } catch (final IOException e) {
                        // ignore this. File IO errors will be reported later anyway
                    }
                }
            }
        }

        return new Context(options, errors, wout, werr, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Preprocess the command line arguments passed in by the shell. This method checks, for the first non-option
     * argument, whether the file denoted by it begins with a shebang line. If so, it is assumed that execution in
     * shebang mode is intended. The consequence of this is that the identified script file will be treated as the
     * <em>only</em> script file, and all subsequent arguments will be regarded as arguments to the script.
     * <p>
     * This method canonicalizes the command line arguments to the form {@code <options> <script> -- <arguments>} if a
     * shebang script is identified. On platforms that pass shebang arguments as single strings, the shebang arguments
     * will be broken down into single arguments; whitespace is used as separator.
     * <p>
     * Shebang mode is entered regardless of whether the script is actually run directly from the shell, or indirectly
     * via the {@code jjs} executable. It is the user's / script author's responsibility to ensure that the arguments
     * given on the shebang line do not lead to a malformed argument sequence. In particular, the shebang arguments
     * should not contain any whitespace for purposes other than separating arguments, as the different platforms deal
     * with whitespace in different and incompatible ways.
     * <p>
     * @implNote Example:<ul>
     * <li>Shebang line in {@code script.js}: {@code #!/path/to/jjs --language=es6}</li>
     * <li>Command line: {@code ./script.js arg2}</li>
     * <li>{@code args} array passed to Nashorn: {@code --language=es6,./script.js,arg}</li>
     * <li>Required canonicalized arguments array: {@code --language=es6,./script.js,--,arg2}</li>
     * </ul>
     *
     * @param args the command line arguments as passed into Nashorn.
     * @return the passed and possibly canonicalized argument list
     */
    private static String[] preprocessArgs(final String[] args) {
        if (args.length == 0) {
            return args;
        }

        final List<String> processedArgs = new ArrayList<>();
        processedArgs.addAll(Arrays.asList(args));

        // Nashorn supports passing multiple shebang arguments. On platforms that pass anything following the
        // shebang interpreter notice as one argument, the first element of the argument array needs to be special-cased
        // as it might actually contain several arguments. Mac OS X splits shebang arguments, other platforms don't.
        // This special handling is also only necessary if the first argument actually starts with an option.
        if (args[0].startsWith("-") && !System.getProperty("os.name", "generic").startsWith("Mac OS X")) {
            processedArgs.addAll(0, tokenizeString(processedArgs.remove(0)));
        }

        int shebangFilePos = -1; // -1 signifies "none found"
        // identify a shebang file and its position in the arguments array (if any)
        for (int i = 0; i < processedArgs.size(); ++i) {
            final String a = processedArgs.get(i);
            if (!a.startsWith("-")) {
                final Path p = Paths.get(a);
                String l = "";
                try (final BufferedReader r = Files.newBufferedReader(p)) {
                    l = r.readLine();
                } catch (final IOException ioe) {
                    // ignore
                }
                if (l != null && l.startsWith("#!")) {
                    shebangFilePos = i;
                }
                // We're only checking the first non-option argument. If it's not a shebang file, we're in normal
                // execution mode.
                break;
            }
        }
        if (shebangFilePos != -1) {
            // Insert the argument separator after the shebang script file.
            processedArgs.add(shebangFilePos + 1, "--");
        }
        return processedArgs.stream().toArray(String[]::new);
    }

    public static List<String> tokenizeString(final String str) {
        final StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(str));
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.commentChar('#');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        final List<String> tokenList = new ArrayList<>();
        final StringBuilder toAppend = new StringBuilder();
        while (nextToken(tokenizer) != StreamTokenizer.TT_EOF) {
            final String s = tokenizer.sval;
            // The tokenizer understands about honoring quoted strings and recognizes
            // them as one token that possibly contains multiple space-separated words.
            // It does not recognize quoted spaces, though, and will split after the
            // escaping \ character. This is handled here.
            if (s.endsWith("\\")) {
                // omit trailing \, append space instead
                toAppend.append(s, 0, s.length() - 1).append(' ');
            } else {
                tokenList.add(toAppend.append(s).toString());
                toAppend.setLength(0);
            }
        }
        if (toAppend.length() != 0) {
            tokenList.add(toAppend.toString());
        }
        return tokenList;
    }

    private static int nextToken(final StreamTokenizer tokenizer) {
        try {
            return tokenizer.nextToken();
        } catch (final IOException ioe) {
            return StreamTokenizer.TT_EOF;
        }
    }

    /**
     * Compiles the given script files in the command line
     * This is called only when using the --compile-only flag
     *
     * @param context the nashorn context
     * @param global the global scope
     * @param files the list of script files to compile
     *
     * @return error code
     * @throws IOException when any script file read results in I/O error
     */
    private static int compileScripts(final Context context, final Global global, final List<String> files) throws IOException {
        final Global oldGlobal = Context.getGlobal();
        final boolean globalChanged = (oldGlobal != global);
        final ScriptEnvironment env = context.getEnv();
        try {
            if (globalChanged) {
                Context.setGlobal(global);
            }
            final ErrorManager errors = context.getErrorManager();

            // For each file on the command line.
            for (final String fileName : files) {
                final FunctionNode functionNode = new Parser(env, sourceFor(fileName, new File(fileName)), errors, env._strict, 0, context.getLogger(Parser.class)).parse();

                if (errors.getNumberOfErrors() != 0) {
                    return COMPILATION_ERROR;
                }

                Compiler.forNoInstallerCompilation(
                       context,
                       functionNode.getSource(),
                       env._strict | functionNode.isStrict()).
                       compile(functionNode, CompilationPhases.COMPILE_ALL_NO_INSTALL);

                if (env._print_ast) {
                    context.getErr().println(new ASTWriter(functionNode));
                }

                if (env._print_parse) {
                    context.getErr().println(new PrintVisitor(functionNode));
                }

                if (errors.getNumberOfErrors() != 0) {
                    return COMPILATION_ERROR;
                }
            }
        } finally {
            env.getOut().flush();
            env.getErr().flush();
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }

        return SUCCESS;
    }

    /**
     * Runs the given JavaScript files in the command line
     *
     * @param context the nashorn context
     * @param global the global scope
     * @param files the list of script files to run
     *
     * @return error code
     * @throws IOException when any script file read results in I/O error
     */
    private int runScripts(final Context context, final Global global, final List<String> files) throws IOException {
        final Global oldGlobal = Context.getGlobal();
        final boolean globalChanged = (oldGlobal != global);
        try {
            if (globalChanged) {
                Context.setGlobal(global);
            }
            final ErrorManager errors = context.getErrorManager();

            // For each file on the command line.
            for (final String fileName : files) {
                if ("-".equals(fileName)) {
                    final int res = readEvalPrint(context, global);
                    if (res != SUCCESS) {
                        return res;
                    }
                    continue;
                }

                final File file = new File(fileName);
                final ScriptFunction script = context.compileScript(sourceFor(fileName, file), global);
                if (script == null || errors.getNumberOfErrors() != 0) {
                    if (context.getEnv()._parse_only && !errors.hasErrors()) {
                        continue; // No error, continue to consume all files in list
                    }
                    return COMPILATION_ERROR;
                }

                try {
                    apply(script, global);
                } catch (final NashornException e) {
                    errors.error(e.toString());
                    if (context.getEnv()._dump_on_error) {
                        e.printStackTrace(context.getErr());
                    }

                    return RUNTIME_ERROR;
                }
            }
        } finally {
            context.getOut().flush();
            context.getErr().flush();
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }

        return SUCCESS;
    }

    /**
     * Runs launches "fx:bootstrap.js" with the given JavaScript files provided
     * as arguments.
     *
     * @param context the nashorn context
     * @param global the global scope
     * @param files the list of script files to provide
     *
     * @return error code
     * @throws IOException when any script file read results in I/O error
     */
    private static int runFXScripts(final Context context, final Global global, final List<String> files) throws IOException {
        final Global oldGlobal = Context.getGlobal();
        final boolean globalChanged = (oldGlobal != global);
        try {
            if (globalChanged) {
                Context.setGlobal(global);
            }

            global.addOwnProperty("$GLOBAL", Property.NOT_ENUMERABLE, global);
            global.addOwnProperty("$SCRIPTS", Property.NOT_ENUMERABLE, files);
            context.load(global, "fx:bootstrap.js");
        } catch (final NashornException e) {
            context.getErrorManager().error(e.toString());
            if (context.getEnv()._dump_on_error) {
                e.printStackTrace(context.getErr());
            }

            return RUNTIME_ERROR;
        } finally {
            context.getOut().flush();
            context.getErr().flush();
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }

        return SUCCESS;
    }

    /**
     * Hook to ScriptFunction "apply". A performance metering shell may
     * introduce enter/exit timing here.
     *
     * @param target target function for apply
     * @param self self reference for apply
     *
     * @return result of the function apply
     */
    protected Object apply(final ScriptFunction target, final Object self) {
        return ScriptRuntime.apply(target, self);
    }

    /**
     * Parse potentially partial code and keep track of the start of last expression.
     * This 'partial' parsing support is meant to be used for code-completion.
     *
     * @param context the nashorn context
     * @param code code that is to be parsed
     * @return the start index of the last expression parsed in the (incomplete) code.
     */
    @Override
    public final int getLastExpressionStart(final Context context, final String code) {
        final int[] exprStart = { -1 };

        final Parser p = new Parser(context.getEnv(), sourceFor("<partial_code>", code),new Context.ThrowErrorManager()) {
            @Override
            protected Expression expression() {
                exprStart[0] = this.start;
                return super.expression();
            }

            @Override
            protected Expression assignmentExpression(final boolean noIn) {
                exprStart[0] = this.start;
                return super.assignmentExpression(noIn);
            }
        };

        try {
            p.parse();
        } catch (final Exception ignored) {
            // throw any parser exception, but we are partial parsing anyway
        }

        return exprStart[0];
    }


    /**
     * read-eval-print loop for Nashorn shell.
     *
     * @param context the nashorn context
     * @param global  global scope object to use
     * @return return code
     */
    protected int readEvalPrint(final Context context, final Global global) {
        final String prompt = bundle.getString("shell.prompt");
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final PrintWriter err = context.getErr();
        final Global oldGlobal = Context.getGlobal();
        final boolean globalChanged = (oldGlobal != global);
        final ScriptEnvironment env = context.getEnv();

        try {
            if (globalChanged) {
                Context.setGlobal(global);
            }

            global.addShellBuiltins();

            while (true) {
                err.print(prompt);
                err.flush();

                String source = "";
                try {
                    source = in.readLine();
                } catch (final IOException ioe) {
                    err.println(ioe);
                }

                if (source == null) {
                    break;
                }

                if (source.isEmpty()) {
                    continue;
                }

                try {
                    final Object res = context.eval(global, source, global, "<shell>");
                    if (res != ScriptRuntime.UNDEFINED) {
                        err.println(toString(res, global));
                    }
                } catch (final Exception e) {
                    err.println(e);
                    if (env._dump_on_error) {
                        e.printStackTrace(err);
                    }
                }
            }
        } finally {
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }

        return SUCCESS;
    }

    /**
     * Converts {@code result} to a printable string. The reason we don't use {@link JSType#toString(Object)}
     * or {@link ScriptRuntime#safeToString(Object)} is that we want to be able to render Symbol values
     * even if they occur within an Array, and therefore have to implement our own Array to String
     * conversion.
     *
     * @param result the result
     * @param global the global object
     * @return the string representation
     */
    protected static String toString(final Object result, final Global global) {
        if (result instanceof Symbol) {
            // Normal implicit conversion of symbol to string would throw TypeError
            return result.toString();
        }

        if (result instanceof NativeSymbol) {
            return JSType.toPrimitive(result).toString();
        }

        if (isArrayWithDefaultToString(result, global)) {
            // This should yield the same string as Array.prototype.toString but
            // will not throw if the array contents include symbols.
            final StringBuilder sb = new StringBuilder();
            final Iterator<Object> iter = ArrayLikeIterator.arrayLikeIterator(result, true);

            while (iter.hasNext()) {
                final Object obj = iter.next();

                if (obj != null && obj != ScriptRuntime.UNDEFINED) {
                    sb.append(toString(obj, global));
                }

                if (iter.hasNext()) {
                    sb.append(',');
                }
            }

            return sb.toString();
        }

        return JSType.toString(result);
    }

    private static boolean isArrayWithDefaultToString(final Object result, final Global global) {
        if (result instanceof ScriptObject) {
            final ScriptObject sobj = (ScriptObject) result;
            return sobj.isArray() && sobj.get("toString") == global.getArrayPrototype().get("toString");
        }
        return false;
    }
}
