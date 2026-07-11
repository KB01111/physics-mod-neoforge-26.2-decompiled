package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Preprocessor implements Closeable {
   private static final Logger LOG = LoggerFactory.getLogger(Preprocessor.class);
   private static final Source INTERNAL = new Source() {
      @Override
      public Token token() throws IOException, LexerException {
         throw new LexerException("Cannot read from " + this.getName());
      }

      @Override
      public String getPath() {
         return "<internal-data>";
      }

      @Override
      public String getName() {
         return "internal data";
      }
   };
   private static final Macro __LINE__ = new Macro(INTERNAL, "__LINE__");
   private static final Macro __FILE__ = new Macro(INTERNAL, "__FILE__");
   private static final Macro __COUNTER__ = new Macro(INTERNAL, "__COUNTER__");
   private final List<Source> inputs;
   private final Map<String, Macro> macros;
   private final Stack<State> states;
   private Source source;
   private int counter;
   private final Set<String> onceseenpaths = new HashSet<>();
   private final List<VirtualFile> includes = new ArrayList<>();
   private List<String> quoteincludepath;
   private List<String> sysincludepath;
   private List<String> frameworkspath;
   private final Set<Feature> features;
   private final Set<Warning> warnings;
   private VirtualFileSystem filesystem;
   private PreprocessorListener listener;
   private Token source_token;
   private Token expr_token = null;

   public Preprocessor() {
      this.inputs = new ArrayList<>();
      this.macros = new HashMap<>();
      this.macros.put(__LINE__.getName(), __LINE__);
      this.macros.put(__FILE__.getName(), __FILE__);
      this.macros.put(__COUNTER__.getName(), __COUNTER__);
      this.states = new Stack<>();
      this.states.push(new State());
      this.source = null;
      this.counter = 0;
      this.quoteincludepath = new ArrayList<>();
      this.sysincludepath = new ArrayList<>();
      this.frameworkspath = new ArrayList<>();
      this.features = EnumSet.noneOf(Feature.class);
      this.warnings = EnumSet.noneOf(Warning.class);
      this.filesystem = new JavaFileSystem();
      this.listener = null;
   }

   public Preprocessor(@NonNull Source initial) {
      this();
      this.addInput(initial);
   }

   public Preprocessor(@NonNull File file) throws IOException {
      this(new FileLexerSource(file));
   }

   public void setFileSystem(@NonNull VirtualFileSystem filesystem) {
      this.filesystem = filesystem;
   }

   @NonNull
   public VirtualFileSystem getFileSystem() {
      return this.filesystem;
   }

   public void setListener(@NonNull PreprocessorListener listener) {
      this.listener = listener;

      for (Source s = this.source; s != null; s = s.getParent()) {
         s.init(this);
      }
   }

   @NonNull
   public PreprocessorListener getListener() {
      return this.listener;
   }

   @NonNull
   public Set<Feature> getFeatures() {
      return this.features;
   }

   public void addFeature(@NonNull Feature f) {
      this.features.add(f);
   }

   public void addFeatures(@NonNull Collection<Feature> f) {
      this.features.addAll(f);
   }

   public void addFeatures(Feature... f) {
      this.addFeatures(Arrays.asList(f));
   }

   public boolean getFeature(@NonNull Feature f) {
      return this.features.contains(f);
   }

   @NonNull
   public Set<Warning> getWarnings() {
      return this.warnings;
   }

   public void addWarning(@NonNull Warning w) {
      this.warnings.add(w);
   }

   public void addWarnings(@NonNull Collection<Warning> w) {
      this.warnings.addAll(w);
   }

   public boolean getWarning(@NonNull Warning w) {
      return this.warnings.contains(w);
   }

   public void addInput(@NonNull Source source) {
      source.init(this);
      this.inputs.add(source);
   }

   public void addInput(@NonNull File file) throws IOException {
      this.addInput(new FileLexerSource(file));
   }

   protected void error(int line, int column, @NonNull String msg) throws LexerException {
      if (this.listener != null) {
         this.listener.handleError(this.source, line, column, msg);
      } else {
         throw new LexerException("Error at " + line + ":" + column + ": " + msg);
      }
   }

   protected void error(@NonNull Token tok, @NonNull String msg) throws LexerException {
      this.error(tok.getLine(), tok.getColumn(), msg);
   }

   protected void warning(int line, int column, @NonNull String msg) throws LexerException {
      if (this.warnings.contains(Warning.ERROR)) {
         this.error(line, column, msg);
      } else {
         if (this.listener == null) {
            throw new LexerException("Warning at " + line + ":" + column + ": " + msg);
         }

         this.listener.handleWarning(this.source, line, column, msg);
      }
   }

   protected void warning(@NonNull Token tok, @NonNull String msg) throws LexerException {
      this.warning(tok.getLine(), tok.getColumn(), msg);
   }

   public void addMacro(@NonNull Macro m) throws LexerException {
      String name = m.getName();
      if ("defined".equals(name)) {
         throw new LexerException("Cannot redefine name 'defined'");
      } else {
         this.macros.put(m.getName(), m);
      }
   }

   public void addMacro(@NonNull String name, @NonNull String value) throws LexerException {
      try {
         Macro m = new Macro(name);
         StringLexerSource s = new StringLexerSource(value);

         while (true) {
            Token tok = s.token();
            if (tok.getType() == 265) {
               this.addMacro(m);
               return;
            }

            m.addToken(tok);
         }
      } catch (IOException var6) {
         throw new LexerException(var6);
      }
   }

   public void addMacro(@NonNull String name) throws LexerException {
      this.addMacro(name, "1");
   }

   public void setQuoteIncludePath(@NonNull List<String> path) {
      this.quoteincludepath = path;
   }

   @NonNull
   public List<String> getQuoteIncludePath() {
      return this.quoteincludepath;
   }

   public void setSystemIncludePath(@NonNull List<String> path) {
      this.sysincludepath = path;
   }

   @NonNull
   public List<String> getSystemIncludePath() {
      return this.sysincludepath;
   }

   public void setFrameworksPath(@NonNull List<String> path) {
      this.frameworkspath = path;
   }

   @NonNull
   public List<String> getFrameworksPath() {
      return this.frameworkspath;
   }

   @NonNull
   public Map<String, Macro> getMacros() {
      return this.macros;
   }

   public Macro getMacro(@NonNull String name) {
      return this.macros.get(name);
   }

   @NonNull
   public List<? extends VirtualFile> getIncludes() {
      return this.includes;
   }

   private void push_state() {
      State top = this.states.peek();
      this.states.push(new State(top));
   }

   private void pop_state() throws LexerException {
      State s = this.states.pop();
      if (this.states.isEmpty()) {
         this.error(0, 0, "#endif without #if");
         this.states.push(s);
      }
   }

   private boolean isActive() {
      State state = this.states.peek();
      return state.isParentActive() && state.isActive();
   }

   protected Source getSource() {
      return this.source;
   }

   protected void push_source(@NonNull Source source, boolean autopop) {
      source.init(this);
      source.setParent(this.source, autopop);
      if (this.listener != null) {
         this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.SUSPEND);
      }

      this.source = source;
      if (this.listener != null) {
         this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.PUSH);
      }
   }

   protected Token pop_source(boolean linemarker) throws IOException {
      if (this.listener != null) {
         this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.POP);
      }

      Source s = this.source;
      this.source = s.getParent();
      s.close();
      if (this.listener != null && this.source != null) {
         this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.RESUME);
      }

      Source t = this.getSource();
      return this.getFeature(Feature.LINEMARKERS) && s.isNumbered() && t != null ? this.line_token(t.getLine(), t.getName(), " 2") : null;
   }

   protected void pop_source() throws IOException {
      this.pop_source(false);
   }

   @NonNull
   private Token next_source() {
      if (this.inputs.isEmpty()) {
         return new Token(265);
      } else {
         Source s = this.inputs.remove(0);
         this.push_source(s, true);
         return this.line_token(s.getLine(), s.getName(), " 1");
      }
   }

   @NonNull
   private Token line_token(int line, String name, @NonNull String extra) {
      StringBuilder buf = new StringBuilder();
      buf.append("#line ").append(line).append(" \"");
      if (name == null) {
         buf.append("<no file>");
      } else {
         MacroTokenSource.escape(buf, name);
      }

      buf.append("\"").append(extra).append("\n");
      return new Token(299, line, 0, buf.toString(), null);
   }

   @NonNull
   private Token source_token() throws IOException, LexerException {
      if (this.source_token != null) {
         Token tok = this.source_token;
         this.source_token = null;
         if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("Returning unget token " + tok);
         }

         return tok;
      } else {
         while (true) {
            Source s = this.getSource();
            if (s == null) {
               Token t = this.next_source();
               if (t.getType() != 299 || this.getFeature(Feature.LINEMARKERS)) {
                  return t;
               }
            } else {
               Token tok = s.token();
               if (tok.getType() != 265 || !s.isAutopop()) {
                  if (this.getFeature(Feature.DEBUG)) {
                     LOG.debug("Returning fresh token " + tok);
                  }

                  return tok;
               }

               Token mark = this.pop_source(true);
               if (mark != null) {
                  return mark;
               }
            }
         }
      }
   }

   private void source_untoken(Token tok) {
      if (this.source_token != null) {
         throw new IllegalStateException("Cannot return two tokens");
      } else {
         this.source_token = tok;
      }
   }

   private boolean isWhite(Token tok) {
      int type = tok.getType();
      return type == 294 || type == 260 || type == 261;
   }

   private Token source_token_nonwhite() throws IOException, LexerException {
      Token tok;
      do {
         tok = this.source_token();
      } while (this.isWhite(tok));

      return tok;
   }

   private Token source_skipline(boolean white) throws IOException, LexerException {
      Source s = this.getSource();
      Token tok = s.skipline(white);
      if (tok.getType() == 265 && s.isAutopop()) {
         Token mark = this.pop_source(true);
         if (mark != null) {
            return mark;
         }
      }

      return tok;
   }

   private boolean macro(Macro m, Token orig) throws IOException, LexerException {
      List<Argument> args;
      if (!m.isFunctionLike()) {
         args = null;
      } else {
         label116:
         while (true) {
            Token tok = this.source_token();
            switch (tok.getType()) {
               case 40:
                  tok = this.source_token_nonwhite();
                  if (tok.getType() == 41 && m.getArgs() <= 0) {
                     args = null;
                     break label116;
                  } else {
                     args = new ArrayList<>();
                     Argument arg = new Argument();
                     int depth = 0;
                     boolean space = false;

                     while (true) {
                        switch (tok.getType()) {
                           case 40:
                              depth++;
                              arg.addToken(tok);
                              space = false;
                              break;
                           case 41:
                              if (depth == 0) {
                                 args.add(arg);
                                 if (args.size() != m.getArgs()) {
                                    if (!m.isVariadic()) {
                                       this.error(tok, "macro " + m.getName() + " has " + m.getArgs() + " parameters but given " + args.size() + " args");
                                       return false;
                                    }

                                    if (args.size() != m.getArgs() - 1) {
                                       this.error(
                                          tok,
                                          "variadic macro "
                                             + m.getName()
                                             + " has at least "
                                             + (m.getArgs() - 1)
                                             + " parameters but given "
                                             + args.size()
                                             + " args"
                                       );
                                       return false;
                                    }

                                    args.add(new Argument());
                                 }

                                 Iterator c = args.iterator();

                                 while (true) {
                                    if (!c.hasNext()) {
                                       break label116;
                                    }

                                    Argument a = (Argument)c.next();
                                    a.expand(this);
                                 }
                              }

                              depth--;
                              arg.addToken(tok);
                              space = false;
                              break;
                           case 44:
                              if (depth == 0) {
                                 if (m.isVariadic() && args.size() == m.getArgs() - 1) {
                                    arg.addToken(tok);
                                 } else {
                                    args.add(arg);
                                    arg = new Argument();
                                 }
                              } else {
                                 arg.addToken(tok);
                              }

                              space = false;
                              break;
                           case 260:
                           case 261:
                           case 284:
                           case 294:
                              space = true;
                              break;
                           case 265:
                              this.error(tok, "EOF in macro args");
                              return false;
                           default:
                              if (space && !arg.isEmpty()) {
                                 arg.addToken(Token.space);
                              }

                              arg.addToken(tok);
                              space = false;
                        }

                        tok = this.source_token();
                     }
                  }
               case 260:
               case 261:
               case 284:
               case 294:
                  break;
               default:
                  this.source_untoken(tok);
                  return false;
            }
         }
      }

      if (m == __LINE__) {
         this.push_source(
            new FixedTokenSource(
               new Token(272, orig.getLine(), orig.getColumn(), Integer.toString(orig.getLine()), new NumericValue(10, Integer.toString(orig.getLine())))
            ),
            true
         );
      } else if (m == __FILE__) {
         StringBuilder buf = new StringBuilder("\"");
         String name = this.getSource().getName();
         if (name == null) {
            name = "<no file>";
         }

         for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            switch (c) {
               case '"':
                  buf.append("\\\"");
                  break;
               case '\\':
                  buf.append("\\\\");
                  break;
               default:
                  buf.append(c);
            }
         }

         buf.append("\"");
         String text = buf.toString();
         this.push_source(new FixedTokenSource(new Token(292, orig.getLine(), orig.getColumn(), text, text)), true);
      } else if (m == __COUNTER__) {
         int value = this.counter++;
         this.push_source(
            new FixedTokenSource(new Token(272, orig.getLine(), orig.getColumn(), Integer.toString(value), new NumericValue(10, Integer.toString(value)))),
            true
         );
      } else {
         this.push_source(new MacroTokenSource(m, args), true);
      }

      return true;
   }

   @NonNull
   List<Token> expand(@NonNull List<Token> arg) throws IOException, LexerException {
      List<Token> expansion = new ArrayList<>();
      boolean space = false;
      this.push_source(new FixedTokenSource(arg), false);

      while (true) {
         Token tok = this.expanded_token();
         switch (tok.getType()) {
            case 260:
            case 261:
            case 294:
               space = true;
               break;
            case 265:
               this.pop_source(false);
               return expansion;
            default:
               if (space && !expansion.isEmpty()) {
                  expansion.add(Token.space);
               }

               expansion.add(tok);
               space = false;
         }
      }
   }

   private Token define() throws IOException, LexerException {
      Token tok = this.source_token_nonwhite();
      if (tok.getType() != 270) {
         this.error(tok, "Expected identifier");
         return this.source_skipline(false);
      } else {
         String name = tok.getText();
         if ("defined".equals(name)) {
            this.error(tok, "Cannot redefine name 'defined'");
            return this.source_skipline(false);
         } else {
            Macro m = new Macro(this.getSource(), name);
            tok = this.source_token();
            List<String> args;
            if (tok.getType() != 40) {
               args = Collections.emptyList();
               this.source_untoken(tok);
            } else {
               tok = this.source_token_nonwhite();
               if (tok.getType() != 41) {
                  args = new ArrayList<>();

                  label96:
                  while (true) {
                     switch (tok.getType()) {
                        case 264:
                           args.add("__VA_ARGS__");
                           this.source_untoken(tok);
                           break;
                        case 265:
                        case 284:
                           this.error(tok, "Unterminated macro parameter list");
                           return tok;
                        case 270:
                           args.add(tok.getText());
                           break;
                        default:
                           this.error(tok, "error in macro parameters: " + tok.getText());
                           return this.source_skipline(false);
                     }

                     tok = this.source_token_nonwhite();
                     switch (tok.getType()) {
                        case 41:
                           break label96;
                        case 44:
                           tok = this.source_token_nonwhite();
                           break;
                        case 264:
                           tok = this.source_token_nonwhite();
                           if (tok.getType() != 41) {
                              this.error(tok, "ellipsis must be on last argument");
                           }

                           m.setVariadic(true);
                           break label96;
                        case 265:
                        case 284:
                           this.error(tok, "Unterminated macro parameters");
                           return tok;
                        default:
                           this.error(tok, "Bad token in macro parameters: " + tok.getText());
                           return this.source_skipline(false);
                     }
                  }
               } else {
                  assert tok.getType() == 41 : "Expected ')'";

                  args = Collections.emptyList();
               }

               m.setArgs(args);
            }

            boolean space = false;
            boolean paste = false;
            tok = this.source_token_nonwhite();

            while (true) {
               switch (tok.getType()) {
                  case 35:
                     if (space) {
                        m.addToken(Token.space);
                     }

                     space = false;
                     Token la = this.source_token_nonwhite();
                     int idx;
                     if (la.getType() == 270 && (idx = args.indexOf(la.getText())) != -1) {
                        m.addToken(new Token(298, la.getLine(), la.getColumn(), "#" + la.getText(), idx));
                     } else {
                        m.addToken(tok);
                        this.source_untoken(la);
                     }
                     break;
                  case 260:
                  case 261:
                  case 294:
                     if (!paste) {
                        space = true;
                     }
                     break;
                  case 265:
                  case 284:
                     if (this.getFeature(Feature.DEBUG)) {
                        LOG.debug("Defined macro " + m);
                     }

                     this.addMacro(m);
                     return tok;
                  case 270: {
                     if (space) {
                        m.addToken(Token.space);
                     }

                     space = false;
                     paste = false;
                     idx = args.indexOf(tok.getText());
                     if (idx == -1) {
                        m.addToken(tok);
                     } else {
                        m.addToken(new Token(296, tok.getLine(), tok.getColumn(), tok.getText(), idx));
                     }
                     break;
                  }
                  case 286:
                     space = false;
                     paste = true;
                     m.addPaste(new Token(297, tok.getLine(), tok.getColumn(), "##", null));
                     break;
                  default:
                     if (space) {
                        m.addToken(Token.space);
                     }

                     space = false;
                     paste = false;
                     m.addToken(tok);
               }

               tok = this.source_token();
            }
         }
      }
   }

   @NonNull
   private Token undef() throws IOException, LexerException {
      Token tok = this.source_token_nonwhite();
      if (tok.getType() != 270) {
         this.error(tok, "Expected identifier, not " + tok.getText());
         if (tok.getType() == 284 || tok.getType() == 265) {
            return tok;
         }
      } else {
         Macro m = this.getMacro(tok.getText());
         if (m != null) {
            this.macros.remove(m.getName());
         }
      }

      return this.source_skipline(true);
   }

   protected boolean include(@NonNull VirtualFile file) throws IOException {
      if (!file.isFile()) {
         return false;
      } else {
         if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("pp: including " + file);
         }

         this.includes.add(file);
         this.push_source(file.getSource(), true);
         return true;
      }
   }

   protected boolean include(@NonNull Iterable<String> path, @NonNull String name) throws IOException {
      for (String dir : path) {
         VirtualFile file = this.getFileSystem().getFile(dir, name);
         if (this.include(file)) {
            return true;
         }
      }

      return false;
   }

   private void include(String parent, int line, @NonNull String name, boolean quoted, boolean next) throws IOException, LexerException {
      if (name.startsWith("/")) {
         VirtualFile file = this.filesystem.getFile(name);
         if (!this.include(file)) {
            StringBuilder buf = new StringBuilder();
            buf.append("File not found: ").append(name);
            this.error(line, 0, buf.toString());
         }
      } else {
         VirtualFile pdir = null;
         if (quoted) {
            if (parent != null) {
               VirtualFile pfile = this.filesystem.getFile(parent);
               pdir = pfile.getParentFile();
            }

            if (pdir != null) {
               VirtualFile ifile = pdir.getChildFile(name);
               if (this.include(ifile)) {
                  return;
               }
            }

            if (this.include(this.quoteincludepath, name)) {
               return;
            }
         } else {
            int idx = name.indexOf(47);
            if (idx != -1) {
               String frameworkName = name.substring(0, idx);
               String headerName = name.substring(idx + 1);
               String headerPath = frameworkName + ".framework/Headers/" + headerName;
               if (this.include(this.frameworkspath, headerPath)) {
                  return;
               }
            }
         }

         if (!this.include(this.sysincludepath, name)) {
            StringBuilder buf = new StringBuilder();
            buf.append("File not found: ").append(name);
            buf.append(" in");
            if (quoted) {
               buf.append(" .").append('(').append(pdir).append(')');

               for (String dir : this.quoteincludepath) {
                  buf.append(" ").append(dir);
               }
            }

            for (String dir : this.sysincludepath) {
               buf.append(" ").append(dir);
            }

            this.error(line, 0, buf.toString());
         }
      }
   }

   @NonNull
   private Token include(boolean next) throws IOException, LexerException {
      LexerSource lexer = (LexerSource)this.source;

      try {
         lexer.setInclude(true);
         Token tok = this.token_nonwhite();
         String name;
         boolean quoted;
         if (tok.getType() != 292) {
            if (tok.getType() != 269) {
               this.error(tok, "Expected string or header, not " + tok.getText());
               switch (tok.getType()) {
                  case 265:
                  case 284:
                     return tok;
                  default:
                     return this.source_skipline(false);
               }
            }

            name = (String)tok.getValue();
            quoted = false;
            tok = this.source_skipline(true);
         } else {
            StringBuilder buf = new StringBuilder((String)tok.getValue());

            label87:
            while (true) {
               tok = this.token_nonwhite();
               switch (tok.getType()) {
                  case 265:
                  case 284:
                     name = buf.toString();
                     quoted = true;
                     break label87;
                  case 292:
                     buf.append((String)tok.getValue());
                     break;
                  default:
                     this.warning(tok, "Unexpected token on #include line");
                     return this.source_skipline(false);
               }
            }
         }

         this.include(this.source.getPath(), tok.getLine(), name, quoted, next);
         return !this.getFeature(Feature.LINEMARKERS) ? tok : this.line_token(1, this.source.getName(), " 1");
      } finally {
         lexer.setInclude(false);
      }
   }

   protected void pragma_once(@NonNull Token name) throws IOException, LexerException {
      Source s = this.source;
      if (!this.onceseenpaths.add(s.getPath())) {
         Token mark = this.pop_source(true);
         if (mark != null) {
            this.push_source(new FixedTokenSource(Arrays.asList(mark)), true);
         }
      }
   }

   protected void pragma(@NonNull Token name, @NonNull List<Token> value) throws IOException, LexerException {
      if (this.getFeature(Feature.PRAGMA_ONCE) && "once".equals(name.getText())) {
         this.pragma_once(name);
      } else {
         this.warning(name, "Unknown #pragma: " + name.getText());
      }
   }

   @NonNull
   private Token pragma() throws IOException, LexerException {
      while (true) {
         Token tok = this.source_token();
         switch (tok.getType()) {
            case 260:
            case 261:
            case 294:
               break;
            case 265:
               this.warning(tok, "End of file in #pragma");
               return tok;
            case 270:
               ArrayList value = new ArrayList();

               label24:
               while (true) {
                  tok = this.source_token();
                  switch (tok.getType()) {
                     case 260:
                     case 261:
                        break;
                     case 265:
                        this.warning(tok, "End of file in #pragma");
                     case 284:
                        break label24;
                     case 294:
                        value.add(tok);
                        break;
                     default:
                        value.add(tok);
                  }
               }

               this.pragma(tok, value);
               return tok;
            case 284:
               this.warning(tok, "Empty #pragma");
               return tok;
            default:
               this.warning(tok, "Illegal #pragma " + tok.getText());
               return this.source_skipline(false);
         }
      }
   }

   private void error(@NonNull Token pptok, boolean is_error) throws IOException, LexerException {
      StringBuilder buf = new StringBuilder();
      buf.append('#').append(pptok.getText()).append(' ');
      Token tok = this.source_token_nonwhite();

      while (true) {
         switch (tok.getType()) {
            case 265:
            case 284:
               if (is_error) {
                  this.error(pptok, buf.toString());
               } else {
                  this.warning(pptok, buf.toString());
               }

               return;
            default:
               buf.append(tok.getText());
               tok = this.source_token();
         }
      }
   }

   @NonNull
   private Token expanded_token() throws IOException, LexerException {
      while (true) {
         Token tok = this.source_token();
         if (tok.getType() == 270) {
            Macro m = this.getMacro(tok.getText());
            if (m == null) {
               return tok;
            }

            if (this.source.isExpanding(m)) {
               return tok;
            }

            if (this.macro(m, tok)) {
               continue;
            }
         }

         return tok;
      }
   }

   @NonNull
   private Token expanded_token_nonwhite() throws IOException, LexerException {
      Token tok;
      do {
         tok = this.expanded_token();
      } while (this.isWhite(tok));

      return tok;
   }

   @NonNull
   private Token expr_token() throws IOException, LexerException {
      Token tok = this.expr_token;
      if (tok != null) {
         this.expr_token = null;
      } else {
         tok = this.expanded_token_nonwhite();
         if (tok.getType() == 270 && tok.getText().equals("defined")) {
            Token la = this.source_token_nonwhite();
            boolean paren = false;
            if (la.getType() == 40) {
               paren = true;
               la = this.source_token_nonwhite();
            }

            if (la.getType() != 270) {
               this.error(la, "defined() needs identifier, not " + la.getText());
               tok = new Token(272, la.getLine(), la.getColumn(), "0", new NumericValue(10, "0"));
            } else if (this.macros.containsKey(la.getText())) {
               tok = new Token(272, la.getLine(), la.getColumn(), "1", new NumericValue(10, "1"));
            } else {
               tok = new Token(272, la.getLine(), la.getColumn(), "0", new NumericValue(10, "0"));
            }

            if (paren) {
               la = this.source_token_nonwhite();
               if (la.getType() != 41) {
                  this.expr_untoken(la);
                  this.error(la, "Missing ) in defined(). Got " + la.getText());
               }
            }
         }
      }

      return tok;
   }

   private void expr_untoken(@NonNull Token tok) throws LexerException {
      if (this.expr_token != null) {
         throw new InternalException("Cannot unget two expression tokens.");
      } else {
         this.expr_token = tok;
      }
   }

   private int expr_priority(@NonNull Token op) {
      switch (op.getType()) {
         case 37:
            return 11;
         case 38:
            return 6;
         case 42:
            return 11;
         case 43:
            return 10;
         case 45:
            return 10;
         case 47:
            return 11;
         case 60:
            return 8;
         case 62:
            return 8;
         case 63:
            return 1;
         case 94:
            return 5;
         case 124:
            return 4;
         case 266:
            return 7;
         case 267:
            return 8;
         case 273:
            return 3;
         case 275:
            return 8;
         case 277:
            return 2;
         case 279:
            return 9;
         case 283:
            return 7;
         case 289:
            return 9;
         default:
            return 0;
      }
   }

   private int expr_char(Token token) {
      Object value = token.getValue();
      if (value instanceof Character) {
         return (Character)value;
      } else {
         String text = String.valueOf(value);
         return text.length() == 0 ? 0 : text.charAt(0);
      }
   }

   private long expr(int priority) throws IOException, LexerException {
      Token tok = this.expr_token();
      long lhs;
      switch (tok.getType()) {
         case 33:
            lhs = this.expr(11) == 0L ? 1L : 0L;
            break;
         case 40:
            lhs = this.expr(0);
            tok = this.expr_token();
            if (tok.getType() != 41) {
               this.expr_untoken(tok);
               this.error(tok, "Missing ) in expression. Got " + tok.getText());
               return 0L;
            }
            break;
         case 45:
            lhs = -this.expr(11);
            break;
         case 126:
            lhs = ~this.expr(11);
            break;
         case 259:
            lhs = (long)this.expr_char(tok);
            break;
         case 270:
            if (this.warnings.contains(Warning.UNDEF)) {
               this.warning(tok, "Undefined token '" + tok.getText() + "' encountered in conditional.");
            }

            lhs = 0L;
            break;
         case 272:
            NumericValue value = (NumericValue)tok.getValue();
            lhs = value.longValue();
            break;
         default:
            this.expr_untoken(tok);
            this.error(tok, "Bad token in expression: " + tok.getText());
            return 0L;
      }

      while (true) {
         Token op = this.expr_token();
         int pri = this.expr_priority(op);
         if (pri == 0 || priority >= pri) {
            this.expr_untoken(op);
            return lhs;
         }

         long rhs = this.expr(pri);
         switch (op.getType()) {
            case 37:
               if (rhs == 0L) {
                  this.error(op, "Modulus by zero");
                  lhs = 0L;
               } else {
                  lhs %= rhs;
               }
               break;
            case 38:
               lhs &= rhs;
               break;
            case 42:
               lhs *= rhs;
               break;
            case 43:
               lhs += rhs;
               break;
            case 45:
               lhs -= rhs;
               break;
            case 47:
               if (rhs == 0L) {
                  this.error(op, "Division by zero");
                  lhs = 0L;
               } else {
                  lhs /= rhs;
               }
               break;
            case 60:
               lhs = lhs < rhs ? 1L : 0L;
               break;
            case 62:
               lhs = lhs > rhs ? 1L : 0L;
               break;
            case 63:
               tok = this.expr_token();
               if (tok.getType() != 58) {
                  this.expr_untoken(tok);
                  this.error(tok, "Missing : in conditional expression. Got " + tok.getText());
                  return 0L;
               }

               long falseResult = this.expr(0);
               lhs = lhs != 0L ? rhs : falseResult;
               break;
            case 94:
               lhs ^= rhs;
               break;
            case 124:
               lhs |= rhs;
               break;
            case 266:
               lhs = lhs == rhs ? 1L : 0L;
               break;
            case 267:
               lhs = lhs >= rhs ? 1L : 0L;
               break;
            case 273:
               lhs = lhs != 0L && rhs != 0L ? 1L : 0L;
               break;
            case 275:
               lhs = lhs <= rhs ? 1L : 0L;
               break;
            case 277:
               lhs = lhs == 0L && rhs == 0L ? 0L : 1L;
               break;
            case 279:
               lhs <<= (int)rhs;
               break;
            case 283:
               lhs = lhs != rhs ? 1L : 0L;
               break;
            case 289:
               lhs >>= (int)rhs;
               break;
            default:
               this.error(op, "Unexpected operator " + op.getText());
               return 0L;
         }
      }
   }

   @NonNull
   private Token toWhitespace(@NonNull Token tok) {
      String text = tok.getText();
      int len = text.length();
      boolean cr = false;
      int nls = 0;

      for (int i = 0; i < len; i++) {
         char c = text.charAt(i);
         switch (c) {
            case '\n':
               if (cr) {
                  cr = false;
                  break;
               }
            case '\u000b':
            case '\f':
            case '\u0085':
            case '\u2028':
            case '\u2029':
               cr = false;
               nls++;
               break;
            case '\r':
               cr = true;
               nls++;
         }
      }

      char[] cbuf = new char[nls];
      Arrays.fill(cbuf, '\n');
      return new Token(294, tok.getLine(), tok.getColumn(), new String(cbuf));
   }

   @NonNull
   private Token _token() throws IOException, LexerException {
      while (true) {
         Token tok;
         if (!this.isActive()) {
            Source s = this.getSource();
            if (s == null) {
               Token t = this.next_source();
               if (t.getType() == 299 && !this.getFeature(Feature.LINEMARKERS)) {
                  continue;
               }

               return t;
            }

            try {
               s.setActive(false);
               tok = this.source_token();
            } finally {
               s.setActive(true);
            }

            switch (tok.getType()) {
               case 260:
               case 261:
                  if (this.getFeature(Feature.KEEPALLCOMMENTS)) {
                     return tok;
                  }

                  if (!this.isActive()) {
                     return this.toWhitespace(tok);
                  }

                  if (this.getFeature(Feature.KEEPCOMMENTS)) {
                     return tok;
                  }

                  return this.toWhitespace(tok);
               case 265:
               case 268:
               case 284:
                  break;
               case 294:
                  return tok;
               default:
                  return this.source_skipline(false);
            }
         } else {
            tok = this.source_token();
         }

         switch (tok.getType()) {
            case 33:
            case 37:
            case 38:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 91:
            case 93:
            case 94:
            case 96:
            case 123:
            case 124:
            case 125:
            case 126:
            case 257:
            case 258:
            case 259:
            case 262:
            case 263:
            case 264:
            case 266:
            case 267:
            case 269:
            case 271:
            case 273:
            case 275:
            case 277:
            case 279:
            case 280:
            case 281:
            case 282:
            case 283:
            case 285:
            case 287:
            case 288:
            case 289:
            case 290:
            case 291:
            case 292:
            case 293:
            case 295:
               return tok;
            case 34:
            case 35:
            case 36:
            case 39:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 92:
            case 95:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case 255:
            case 256:
            case 274:
            case 276:
            case 278:
            case 286:
            case 296:
            case 297:
            case 298:
            default:
               throw new InternalException("Bad token " + tok);
            case 260:
            case 261:
               return tok;
            case 265:
               return tok;
            case 268:
               tok = this.source_token_nonwhite();
               switch (tok.getType()) {
                  case 270:
                     PreprocessorCommand ppcmd = PreprocessorCommand.forText(tok.getText());
                     if (ppcmd == null) {
                        this.error(tok, "Unknown preprocessor directive " + tok.getText());
                        return this.source_skipline(false);
                     }

                     switch (ppcmd) {
                        case PP_DEFINE:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           return this.define();
                        case PP_UNDEF:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           return this.undef();
                        case PP_INCLUDE:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           return this.include(false);
                        case PP_INCLUDE_NEXT:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           if (!this.getFeature(Feature.INCLUDENEXT)) {
                              this.error(tok, "Directive include_next not enabled");
                              return this.source_skipline(false);
                           }

                           return this.include(true);
                        case PP_WARNING:
                        case PP_ERROR:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           this.error(tok, ppcmd == PreprocessorCommand.PP_ERROR);
                           continue;
                        case PP_IF:
                           this.push_state();
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           this.expr_token = null;
                           this.states.peek().setActive(this.expr(0) != 0L);
                           tok = this.expr_token();
                           if (tok.getType() == 284) {
                              return tok;
                           }

                           return this.source_skipline(true);
                        case PP_ELIF: {
                           State state = this.states.peek();
                           if (state.sawElse()) {
                              this.error(tok, "#elif after #else");
                              return this.source_skipline(false);
                           }

                           if (!state.isParentActive()) {
                              return this.source_skipline(false);
                           }

                           if (state.isActive()) {
                              state.setParentActive(false);
                              state.setActive(false);
                              return this.source_skipline(false);
                           }

                           this.expr_token = null;
                           state.setActive(this.expr(0) != 0L);
                           tok = this.expr_token();
                           if (tok.getType() == 284) {
                              return tok;
                           }

                           return this.source_skipline(true);
                        }
                        case PP_ELSE: {
                           State elseState = this.states.peek();
                           if (elseState.sawElse()) {
                              this.error(tok, "#else after #else");
                              return this.source_skipline(false);
                           }

                           elseState.setSawElse();
                           elseState.setActive(!elseState.isActive());
                           return this.source_skipline(this.warnings.contains(Warning.ENDIF_LABELS));
                        }
                        case PP_IFDEF: {
                           this.push_state();
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           tok = this.source_token_nonwhite();
                           if (tok.getType() != 270) {
                              this.error(tok, "Expected identifier, not " + tok.getText());
                              return this.source_skipline(false);
                           }

                           String text = tok.getText();
                           boolean exists = this.macros.containsKey(text);
                           this.states.peek().setActive(exists);
                           return this.source_skipline(true);
                        }
                        case PP_IFNDEF: {
                           this.push_state();
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           tok = this.source_token_nonwhite();
                           if (tok.getType() != 270) {
                              this.error(tok, "Expected identifier, not " + tok.getText());
                              return this.source_skipline(false);
                           }

                           String text = tok.getText();
                           boolean exists = this.macros.containsKey(text);
                           this.states.peek().setActive(!exists);
                           return this.source_skipline(true);
                        }
                        case PP_ENDIF:
                           this.pop_state();
                           return this.source_skipline(this.warnings.contains(Warning.ENDIF_LABELS));
                        case PP_LINE:
                           return this.source_skipline(false);
                        case PP_PRAGMA:
                           if (!this.isActive()) {
                              return this.source_skipline(false);
                           }

                           return this.pragma();
                        default:
                           throw new InternalException("Internal error: Unknown directive " + tok);
                     }
                  case 284:
                     continue;
                  default:
                     this.error(tok, "Preprocessor directive not a word " + tok.getText());
                     return this.source_skipline(false);
               }
            case 270:
               Macro m = this.getMacro(tok.getText());
               if (m == null) {
                  return tok;
               }

               if (this.source.isExpanding(m)) {
                  return tok;
               }

               if (this.macro(m, tok)) {
                  break;
               }

               return tok;
            case 272:
               return tok;
            case 284:
            case 294:
               return tok;
            case 299:
               if (!this.getFeature(Feature.LINEMARKERS)) {
                  break;
               }

               return tok;
            case 300:
               if (this.getFeature(Feature.CSYNTAX)) {
                  this.error(tok, String.valueOf(tok.getValue()));
               }

               return tok;
         }
      }
   }

   @NonNull
   private Token token_nonwhite() throws IOException, LexerException {
      Token tok;
      do {
         tok = this._token();
      } while (this.isWhite(tok));

      return tok;
   }

   @NonNull
   public Token token() throws IOException, LexerException {
      Token tok = this._token();
      if (this.getFeature(Feature.DEBUG)) {
         LOG.debug("pp: Returning " + tok);
      }

      return tok;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();

      for (Source s = this.getSource(); s != null; s = s.getParent()) {
         buf.append(" -> ").append(String.valueOf(s)).append("\n");
      }

      Map<String, Macro> macros = new TreeMap<>(this.getMacros());

      for (Macro macro : macros.values()) {
         buf.append("#").append("macro ").append(macro).append("\n");
      }

      return buf.toString();
   }

   @Override
   public void close() throws IOException {
      for (Source s = this.source; s != null; s = s.getParent()) {
         s.close();
      }

      for (Source s : this.inputs) {
         s.close();
      }
   }
}
