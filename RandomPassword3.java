/*
  Random Password #3 - Generate Random Passwords Given Alphabet, Length
  Written by: Keith Fenske, http://kwfenske.github.io/
  Friday, 25 October 2024
  Java class name: RandomPassword3
  Copyright (c) 2024 by Keith Fenske.  Apache License or GNU GPL.

  This is a Java 1.4 application to generate random passwords given an alphabet
  (list of available characters), the length of each password (in characters),
  and the number of passwords required.  The default alphabet uses letters and
  digits that most people can distinguish when written down on a piece of
  paper.  This alphabet does not have to be unique, and may repeat characters
  so they are more likely to appear.

  Our minds look for patterns in random data.  What we see is not always there.
  Duplicates make us think data can not be random.  In fact, the opposite is
  true.  If you generate passwords of length 10 from an alphabet of 30
  characters, then on average, one in four passwords will have an identical
  pair.  Simply ignore passwords that you don't like.

  Apache License or GNU General Public License
  --------------------------------------------
  RandomPassword3 is free software and has been released under the terms and
  conditions of the Apache License (version 2.0 or later) and/or the GNU
  General Public License (GPL, version 2 or later).  This program is
  distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the license(s) for more details.  You should have
  received a copy of the licenses along with this program.  If not, see the
  http://www.apache.org/licenses/ and http://www.gnu.org/licenses/ web pages.

  Graphical Versus Console Application
  ------------------------------------
  The Java command line may contain options or a desired number of passwords.
  If the command line does not say how many passwords to generate, then this
  program runs as a graphical or "GUI" application with the usual dialog boxes
  and windows.  See the "-?" option for a help summary:

      java  RandomPassword3  -?

  The command line has more options than are visible in the graphical
  interface.  An option such as -u16 or -u18 is recommended for the font size.
*/

import java.awt.*;                // older Java GUI support
import java.awt.event.*;          // older Java GUI event support
import java.io.*;                 // standard I/O
import java.util.*;               // calendars, dates, lists, maps, vectors
import java.util.regex.*;         // regular expressions
import javax.swing.*;             // newer Java GUI support
import javax.swing.border.*;      // decorative borders

public class RandomPassword3
{
  /* constants */

  static final String COPYRIGHT_NOTICE =
    "Copyright (c) 2024 by Keith Fenske.  Apache License or GNU GPL.";
  static final String DEFAULT_ALPHABET = // default list of available characters
    "2346789ADEFHJLMNRTVWXYabcdeghknprstuz";
  static final int DEFAULT_HEIGHT = -1; // default window height in pixels
  static final int DEFAULT_LEFT = 50; // default window left position ("x")
  static final int DEFAULT_TOP = 50; // default window top position ("y")
  static final int DEFAULT_WIDTH = -1; // default window width in pixels
  static final int DELAY_DEFAULT = 250; // default password time delay (ms)
  static final int DELAY_LOWER = 0; // minimum password time delay (always zero)
  static final int DELAY_UPPER = 5000; // maximum password time delay (ms)
  static final String EMPTY_STATUS = " "; // message when no status to display,
                                  // ... where empty string hides status bar
  static final int EXIT_FAILURE = -1; // incorrect request or errors found
  static final int EXIT_SUCCESS = 1; // request completed successfully
  static final int EXIT_UNKNOWN = 0; // don't know or nothing really done
  static final String[] FONT_SIZES = {"12", "14", "16", "18", "20", "24", "30",
    "36"};                        // available sizes for output text area
  static final int LENGTH_DEFAULT = 10; // default password length in characters
  static final String[] LENGTH_LIST = {"2", "4", "6", "8", "10", "12", "15",
    "20"};                        // suggested values for password lengths
  static final int LENGTH_LOWER = 1; // minimum password length in characters
  static final int LENGTH_UPPER = 100; // maximum password length in characters
  static final int MIN_FRAME = 200; // minimum window height or width in pixels
  static final int NUMBER_DEFAULT = 5; // default number of passwords generated
  static final String[] NUMBER_LIST = {"1", "2", "5", "10", "20", "50", "100",
    "200"};                       // suggested values for number of passwords
  static final int NUMBER_LOWER = 1; // minimum number of passwords generated
  static final int NUMBER_UPPER = 500; // maximum number of passwords generated
  static final String PROGRAM_TITLE =
    "Generate Random Passwords - by: Keith Fenske";
  static final String SYSTEM_FONT = "Dialog"; // this font is always available
  static final int TIMER_DELAY = 1000; // 1.000 seconds between status updates

  /* class variables */

  static JTextField alphabetDialog; // user's list of available characters
  static JButton cancelButton;    // graphical button for <cancelFlag>
  static boolean cancelFlag;      // our signal from user to stop processing
  static Thread doStartThread;    // separate thread for doStartButton() method
  static JButton exitButton;      // "Exit" button for ending this application
  static JFileChooser fileChooser; // asks for input and output file names
  static JComboBox fontNameDialog; // graphical option for <outputFontName>
  static JComboBox fontSizeDialog; // graphical option for <outputFontSize>
  static JComboBox lengthDialog;  // length of each password in characters
  static JFrame mainFrame;        // this application's GUI window
  static boolean mswinFlag;       // true if running on Microsoft Windows
  static JComboBox numberDialog;  // number of passwords to generate
  static Font outputFont;         // font for output text area
  static String outputFontName;   // preferred font name for output text
  static int outputFontSize;      // normal font size or chosen by user
  static JTextArea outputText;    // generated report while processing
  static JButton saveButton;      // "Save" button for writing output text
  static JButton startButton;     // "Start" button to begin processing
  static JLabel statusDialog;     // status message during extended processing
  static String statusPending;    // will become <statusDialog> after delay
  static javax.swing.Timer statusTimer; // timer for updating status message
  static String userAlphabet;     // alphabet (characters) chosen by user
  static int userDelay;           // time delay between passwords (ms) to make
                                  // ... it look like we're working hard (joke)
  static int userLength;          // password length chosen by user
  static int userNumber;          // number of passwords requested by user

/*
  main() method

  If we are running as a GUI application, set the window layout and then let
  the graphical interface run the show.
*/
  public static void main(String[] args)
  {
    ActionListener action;        // our shared action listener
    Font commonFont;              // font for buttons, labels, status, etc
    String commonFontName;        // preferred font name for buttons, etc
    int commonFontSize;           // normal font size or chosen by user
    boolean consoleFlag;          // true if running as a console application
    Border emptyBorder;           // remove borders around text areas
    int i;                        // index variable
    boolean maximizeFlag;         // true if we maximize our main window
    int windowHeight, windowLeft, windowTop, windowWidth;
                                  // position and size for <mainFrame>
    String word;                  // one parameter from command line

    /* Initialize variables used by both console and GUI applications. */

    cancelFlag = false;           // don't cancel unless user complains
    commonFontName = SYSTEM_FONT; // default to normal font on local system
    commonFontSize = 18;          // preferred font size of buttons, labels
    consoleFlag = false;          // assume no parameters on command line
    mainFrame = null;             // during setup, there is no GUI window
    maximizeFlag = false;         // by default, don't maximize our main window
    mswinFlag = System.getProperty("os.name").startsWith("Windows");
    outputFontName = "Verdana";   // preferred font name for output text area
    outputFontSize = 18;          // starting font size for output text area
    statusPending = EMPTY_STATUS; // begin with no text for <statusDialog>
    userAlphabet = DEFAULT_ALPHABET; // alphabet (characters) chosen by user
    userDelay = DELAY_DEFAULT;    // password time delay (milliseconds)
    userLength = LENGTH_DEFAULT;  // password length chosen by user
    userNumber = NUMBER_DEFAULT;  // number of passwords requested by user
    windowHeight = DEFAULT_HEIGHT; // default window position and size
    windowLeft = DEFAULT_LEFT;
    windowTop = DEFAULT_TOP;
    windowWidth = DEFAULT_WIDTH;

    /* Check command-line parameters for options. */

    for (i = 0; i < args.length; i ++)
    {
      word = args[i].toLowerCase(); // easier to process if consistent case
      if (word.length() == 0)
      {
        /* Ignore empty parameters, which are more common than you might think,
        when programs are being run from inside scripts (command files). */
      }

      else if (word.equals("?") || word.equals("-?") || word.equals("/?")
        || word.equals("-h") || (mswinFlag && word.equals("/h"))
        || word.equals("-help") || (mswinFlag && word.equals("/help")))
      {
        showHelp();               // show help summary
        System.exit(EXIT_UNKNOWN); // exit application after printing help
      }

      else if (word.startsWith("-a") || (mswinFlag && word.startsWith("/a")))
      {
        /* This option is followed by an alphabet of available characters. */

        userAlphabet = args[i].substring(2); // keep letters in original case
        if (userAlphabet.length() == 0)
        {
          System.err.println(
            "Alphabet of available characters can not be empty: "
            + args[i]);           // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
      }

      else if (word.startsWith("-c") || (mswinFlag && word.startsWith("/c")))
      {
        /* This option is followed by the number of characters in each
        password. */

        try { userLength = Integer.parseInt(word.substring(2)); } // unsigned
        catch (NumberFormatException nfe) { userLength = -1; } // illegal value
        if ((userLength < LENGTH_LOWER) || (userLength > LENGTH_UPPER))
        {
          System.err.println("Length of each password must be from "
            + LENGTH_LOWER + " to " + LENGTH_UPPER + " characters: "
            + args[i]);           // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
      }

      else if (word.startsWith("-n") || (mswinFlag && word.startsWith("/n")))
      {
        /* This option is followed by an initial value in the GUI (only) for
        the number of passwords to generate, but does not actually generate
        them (unlike when a number is given as a non-option parameter on the
        command line). */

        try { userNumber = Integer.parseInt(word.substring(2)); } // unsigned
        catch (NumberFormatException nfe) { userNumber = -1; } // illegal value
        if ((userNumber < NUMBER_LOWER) || (userNumber > NUMBER_UPPER))
        {
          System.err.println("Number of passwords to generate must be from "
            + NUMBER_LOWER + " to " + NUMBER_UPPER + ": " + args[i]);
                                  // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
      }

      else if (word.startsWith("-t") || (mswinFlag && word.startsWith("/t")))
      {
        /* This option is followed by a password time delay in milliseconds. */

        try { userDelay = Integer.parseInt(word.substring(2)); } // unsigned
        catch (NumberFormatException nfe) { userDelay = -1; } // illegal value
        if ((userDelay < DELAY_LOWER) || (userDelay > DELAY_UPPER))
        {
          System.err.println("Time delay between passwords must be from "
            + DELAY_LOWER + " to " + DELAY_UPPER + " milliseconds: "
            + args[i]);           // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
      }

      else if (word.startsWith("-u") || (mswinFlag && word.startsWith("/u")))
      {
        /* This option is followed by a font point size that will be used for
        GUI buttons, dialogs, labels, etc. */

        try                       // try to parse remainder as an integer
        {
          commonFontSize = Integer.parseInt(word.substring(2));
        }
        catch (NumberFormatException nfe) // if not a number or bad syntax
        {
          commonFontSize = -1;    // set result to an illegal value
        }
        if ((commonFontSize < 10) || (commonFontSize > 99))
        {
          System.err.println("Dialog font size must be from 10 to 99: "
            + args[i]);           // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
        outputFontSize = commonFontSize; // same for output text area
      }

      else if (word.startsWith("-w") || (mswinFlag && word.startsWith("/w")))
      {
        /* This option is followed by a list of four numbers for the initial
        window position and size.  All values are accepted, but small heights
        or widths will later force the minimum packed size for the layout. */

        Pattern pattern = Pattern.compile(
          "\\s*\\(\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*\\)\\s*");
        Matcher matcher = pattern.matcher(word.substring(2)); // parse option
        if (matcher.matches())    // if option has proper syntax
        {
          windowLeft = Integer.parseInt(matcher.group(1));
          windowTop = Integer.parseInt(matcher.group(2));
          windowWidth = Integer.parseInt(matcher.group(3));
          windowHeight = Integer.parseInt(matcher.group(4));
        }
        else                      // bad syntax or too many digits
        {
          System.err.println("Invalid window position or size: " + args[i]);
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
      }

      else if (word.equals("-x") || (mswinFlag && word.equals("/x")))
        maximizeFlag = true;      // true if we maximize our main window

      else if (word.startsWith("-") || (mswinFlag && word.startsWith("/")))
      {
        System.err.println("Option not recognized: " + args[i]);
        showHelp();               // show help summary
        System.exit(EXIT_FAILURE); // exit application after printing help
      }

      else
      {
        /* Parameter does not look like an option.  Assume this is a number of
        passwords to be generated. */

        consoleFlag = true;       // don't allow GUI methods to be called
        try { userNumber = Integer.parseInt(word); } // parse unsigned integer
        catch (NumberFormatException nfe) { userNumber = -1; } // illegal value
        if ((userNumber < NUMBER_LOWER) || (userNumber > NUMBER_UPPER))
        {
          System.err.println("Number of passwords to generate must be from "
            + NUMBER_LOWER + " to " + NUMBER_UPPER + ": " + args[i]);
                                  // notify user of our arbitrary limits
          showHelp();             // show help summary
          System.exit(EXIT_FAILURE); // exit application after printing help
        }
        generatePasswords();      // call common routine, both console and GUI
        if (cancelFlag) break;    // exit <for> loop if cancel or fatal error
      }
    }

    /* If running as a console application, print a summary of what we found
    and/or changed.  Exit to the system with an integer status. */

    if (consoleFlag)              // did we do anything from command line?
    {
      if (cancelFlag)
        System.exit(EXIT_FAILURE); // cancelled or something went wrong
      else
        System.exit(EXIT_SUCCESS); // we are done and don't need GUI
    }

    /* No parameters were given on the command line that asked us to do real
    work while parsing the options.  Open the graphical user interface (GUI).
    We don't need to be inside an if-then-else construct here because anything
    done as a console application would have called System.exit() above. */

    /* Initialize shared graphical objects. */

    action = new RandomPassword3User(); // create our shared action listener
    commonFont = new Font(commonFontName, Font.PLAIN, commonFontSize);
    emptyBorder = BorderFactory.createEmptyBorder(); // for removing borders
    fileChooser = new JFileChooser(); // create our shared file chooser
    outputFont = new Font(outputFontName, Font.PLAIN, outputFontSize);
    statusTimer = new javax.swing.Timer(TIMER_DELAY, action);
                                  // update status message on clock ticks only

    /* If we ask for a font that is not installed on the local system, Java
    replaces this with its "Dialog" font.  This is only of concern for the
    output text area, because we have a combo box with font names and would
    like the correct name to be selected. */

    if (outputFont.getFamily().equals(outputFontName) == false)
    {
      outputFontName = SYSTEM_FONT; // replace with known good font
      outputFont = new Font(outputFontName, Font.PLAIN, outputFontSize);
    }

    /* Create the graphical interface as a series of smaller panels inside
    bigger panels.  The intermediate panel names are of no lasting importance
    and hence are only numbered (panel261, label354, etc). */

    /* Create a vertical box to stack buttons and options. */

    JPanel panel01 = new JPanel();
    panel01.setLayout(new BoxLayout(panel01, BoxLayout.Y_AXIS));

    /* Create a horizontal panel for the action buttons. */

    JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    startButton = new JButton("Start");
    startButton.addActionListener(action);
    startButton.setFont(commonFont);
    startButton.setMnemonic(KeyEvent.VK_S);
    startButton.setToolTipText("Generate random passwords.");
    panel11.add(startButton);

    panel11.add(Box.createHorizontalStrut(40));

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(action);
    cancelButton.setEnabled(false);
    cancelButton.setFont(commonFont);
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setToolTipText("Stop generating passwords.");
    panel11.add(cancelButton);

    panel11.add(Box.createHorizontalStrut(40));

    exitButton = new JButton("Exit");
    exitButton.addActionListener(action);
    exitButton.setFont(commonFont);
    exitButton.setMnemonic(KeyEvent.VK_X);
    exitButton.setToolTipText("Close this program.");
    panel11.add(exitButton);

    panel01.add(panel11);
    panel01.add(Box.createVerticalStrut(12)); // space between panels

    /* Options for the number of passwords to generate. */

    JPanel panel21 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JLabel label22 = new JLabel("There will be  ");
    label22.setFont(commonFont);
    panel21.add(label22);

    numberDialog = new JComboBox(NUMBER_LIST); // preferred choices
    numberDialog.setEditable(false); // no editing until we set size
    numberDialog.setFont(commonFont);
    numberDialog.setPrototypeDisplayValue("0000"); // allow four digits
//  numberDialog.setSelectedItem(String.valueOf(NUMBER_DEFAULT)); // done later
    numberDialog.setToolTipText("Number of passwords to generate.");
    panel21.add(numberDialog);

    JLabel label23 = new JLabel("  passwords (" + NUMBER_LOWER + "-"
      + NUMBER_UPPER + ").");
    label23.setFont(commonFont);
    panel21.add(label23);

    panel01.add(panel21);
    panel01.add(Box.createVerticalStrut(12)); // space between panels

    /* Options for the length of each password in characters. */

    JPanel panel31 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JLabel label32 = new JLabel("Each password has  ");
    label32.setFont(commonFont);
    panel31.add(label32);

    lengthDialog = new JComboBox(LENGTH_LIST); // preferred choices
    lengthDialog.setEditable(false); // no editing until we set size
    lengthDialog.setFont(commonFont);
    lengthDialog.setPrototypeDisplayValue("0000"); // allow four digits
//  lengthDialog.setSelectedItem(String.valueOf(LENGTH_DEFAULT)); // done later
    lengthDialog.setToolTipText("Length of each password in characters.");
    panel31.add(lengthDialog);

    JLabel label33 = new JLabel("  characters (" + LENGTH_LOWER + "-"
      + LENGTH_UPPER + ").");
    label33.setFont(commonFont);
    panel31.add(label33);

    panel01.add(panel31);
    panel01.add(Box.createVerticalStrut(12)); // space between panels

    /* Options for the user's alphabet or available character set. */

    JPanel panel41 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JLabel label42 = new JLabel("Alphabet: ");
    label42.setFont(commonFont);
    panel41.add(label42);

    alphabetDialog = new JTextField(userAlphabet, 26);
    alphabetDialog.setFont(commonFont);
    alphabetDialog.setMargin(new Insets(2, 4, 2, 4));
    alphabetDialog.setToolTipText("Alphabet of available characters.");
    panel41.add(alphabetDialog);

    panel01.add(panel41);
    panel01.add(Box.createVerticalStrut(12)); // space between panels

    /* Options for the display font and a button for saving the output text. */

    JPanel panel51 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JLabel label52 = new JLabel("Font: ");
    label52.setFont(commonFont);
    panel51.add(label52);

    fontNameDialog = new JComboBox(GraphicsEnvironment
      .getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    fontNameDialog.setEditable(false); // user must select one of our choices
    fontNameDialog.setFont(commonFont);
    fontNameDialog.setSelectedItem(outputFontName); // select default font name
    fontNameDialog.setToolTipText("Font name for output text.");
    fontNameDialog.addActionListener(action); // do last so don't fire early
    panel51.add(fontNameDialog);

    panel51.add(Box.createHorizontalStrut(5));

    fontSizeDialog = new JComboBox(FONT_SIZES); // list of available sizes
    fontSizeDialog.setEditable(false); // user must select one of our choices
    fontSizeDialog.setFont(commonFont);
    fontSizeDialog.setSelectedItem(String.valueOf(outputFontSize));
    fontSizeDialog.setToolTipText("Point size for output text.");
    fontSizeDialog.addActionListener(action); // do last so don't fire early
    panel51.add(fontSizeDialog);

    panel51.add(Box.createHorizontalStrut(40));

    saveButton = new JButton("Save Output...");
    saveButton.addActionListener(action);
    saveButton.setFont(commonFont);
    saveButton.setMnemonic(KeyEvent.VK_O);
    saveButton.setToolTipText("Copy output text to a file.");
    panel51.add(saveButton);

    panel01.add(panel51);
    panel01.add(Box.createVerticalStrut(15)); // space between panels

    /* Bind all of the buttons and options above into a single panel so that
    the layout does not change when the window changes. */

    JPanel panel61 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    panel61.add(panel01);

    /* Create a scrolling text area for the generated output. */

    outputText = new JTextArea(15, 40);
    outputText.setEditable(false); // user can't change this text area
    outputText.setFont(outputFont);
    outputText.setLineWrap(false); // don't wrap text lines
    outputText.setMargin(new Insets(5, 6, 5, 6)); // top, left, bottom, right
    outputText.setText(
      "\nGenerate random passwords given an alphabet (list of available"
      + "\ncharacters), the length of each password (in characters), and the"
      + "\nnumber of passwords required."
      + "\n\nChoose your options; then click the \"Start\" button."
      + "\n\nCopyright (c) 2024 by Keith Fenske.  By using this program, you"
      + "\nagree to terms and conditions of the Apache License and/or GNU"
      + "\nGeneral Public License.\n\n");

    JScrollPane panel71 = new JScrollPane(outputText);
    panel71.setBorder(emptyBorder); // no border necessary here

    /* Create an entire panel just for the status message.  Set margins with a
    BorderLayout, because a few pixels higher or lower can make a difference in
    whether the position of the status text looks correct. */

    statusDialog = new JLabel(statusPending, JLabel.LEFT);
    statusDialog.setFont(commonFont);

    JPanel panel81 = new JPanel(new BorderLayout(0, 0));
    panel81.add(Box.createVerticalStrut(7), BorderLayout.NORTH);
    panel81.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
    panel81.add(statusDialog, BorderLayout.CENTER);
    panel81.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
    panel81.add(Box.createVerticalStrut(2), BorderLayout.SOUTH);

    /* Combine buttons and options with output text.  The text area expands and
    contracts with the window size.  Put our status message at the bottom. */

    JPanel panel91 = new JPanel(new BorderLayout(0, 0));
    panel91.add(panel61, BorderLayout.NORTH); // buttons and options
    panel91.add(panel71, BorderLayout.CENTER); // text area
    panel91.add(panel81, BorderLayout.SOUTH); // status message

    /* Create the main window frame for this application.  We supply our own
    margins using the edges of the frame's border layout. */

    mainFrame = new JFrame(PROGRAM_TITLE);
    Container panel92 = mainFrame.getContentPane(); // where content meets frame
    panel92.setLayout(new BorderLayout(0, 0));
    panel92.add(Box.createVerticalStrut(15), BorderLayout.NORTH); // top margin
    panel92.add(Box.createHorizontalStrut(5), BorderLayout.WEST); // left
    panel92.add(panel91, BorderLayout.CENTER); // actual content in center
    panel92.add(Box.createHorizontalStrut(5), BorderLayout.EAST); // right
    panel92.add(Box.createVerticalStrut(5), BorderLayout.SOUTH); // bottom

    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setLocation(windowLeft, windowTop); // normal top-left corner
    if ((windowHeight < MIN_FRAME) || (windowWidth < MIN_FRAME))
      mainFrame.pack();           // do component layout with minimum size
    else                          // the user has given us a window size
      mainFrame.setSize(windowWidth, windowHeight); // size of normal window
    if (maximizeFlag) mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mainFrame.validate();         // recheck application window layout
    mainFrame.setVisible(true);   // and then show application window

    /* The default width for editable combo boxes is much too wide.  A better
    width is obtained by making the dialog non-editable and fixing the dialog
    at that size, before turning editing back on.  Java 1.4 incorrectly uses
    small spaces for setPrototypeDisplayValue(), probably measured with the
    default GUI font and not our selected font.  Java 5.0 and later have the
    expected size.  Our prototypes in Java 1.4 need four digits; on Java 5.0
    and later, three digits are good, and two digits are almost enough. */

    lengthDialog.setMaximumSize(lengthDialog.getPreferredSize());
    lengthDialog.setEditable(true);
    lengthDialog.setSelectedItem(String.valueOf(userLength));

    numberDialog.setMaximumSize(numberDialog.getPreferredSize());
    numberDialog.setEditable(true);
    numberDialog.setSelectedItem(String.valueOf(userNumber));

    /* Let the graphical interface run the application now. */

    startButton.requestFocusInWindow(); // give keyboard focus to this button

  } // end of main() method

// ------------------------------------------------------------------------- //

/*
  doCancelButton() method

  Call this method if the user wants to stop processing early, perhaps because
  it is taking too long.  We must cleanly terminate any secondary threads.
  Leave whatever output has already been generated in the output text area.
*/
  static void doCancelButton()
  {
    cancelFlag = true;            // tell other threads that all work stops now
    putOutput("Cancelled by user."); // print message and scroll
  }


/*
  doSaveButton() method

  Ask the user for an output file name, create or replace that file, and copy
  the contents of our output text area to that file.  The output file will be
  in the default character set for the system, so if there are special Unicode
  characters in the displayed text (Arabic, Chinese, Eastern European, etc),
  then you are better off copying and pasting the output text directly into a
  Unicode-aware application like Microsoft Word.
*/
  static void doSaveButton()
  {
    FileWriter output;            // output file stream
    File userFile;                // file chosen by the user

    /* Ask the user for an output file name. */

    fileChooser.resetChoosableFileFilters(); // remove any existing filters
    fileChooser.setDialogTitle("Save Output as Text File...");
    fileChooser.setFileHidingEnabled(true); // don't show hidden files
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false); // allow only one file
    if (fileChooser.showSaveDialog(mainFrame) != JFileChooser.APPROVE_OPTION)
      return;                     // user cancelled file selection dialog box
    userFile = fileChooser.getSelectedFile();

    /* See if we can write to the user's chosen file. */

    if (userFile.isDirectory())   // can't write to directories or folders
    {
      JOptionPane.showMessageDialog(mainFrame, (userFile.getName()
        + " is a directory or folder.\nPlease select a normal file."));
      return;
    }
    else if (userFile.isHidden()) // won't write to hidden (protected) files
    {
      JOptionPane.showMessageDialog(mainFrame, (userFile.getName()
        + " is a hidden or protected file.\nPlease select a normal file."));
      return;
    }
    else if (userFile.isFile() == false) // if file doesn't exist
    {
      /* Maybe we can create a new file by this name.  Do nothing here. */
    }
    else if (userFile.canWrite() == false) // file exists, but is read-only
    {
      JOptionPane.showMessageDialog(mainFrame, (userFile.getName()
        + " is locked or write protected.\nCan't write to this file."));
      return;
    }
    else if (JOptionPane.showConfirmDialog(mainFrame, (userFile.getName()
      + " already exists.\nDo you want to replace this with a new file?"))
      != JOptionPane.YES_OPTION)
    {
      return;                     // user cancelled file replacement dialog
    }

    /* Write lines to output file. */

    try                           // catch file I/O errors
    {
      output = new FileWriter(userFile); // try to open output file
      outputText.write(output);   // couldn't be much easier for writing!
      output.close();             // try to close output file
    }
    catch (IOException ioe)
    {
      putOutput("Can't write to text file: " + ioe.getMessage());
    }
  } // end of doSaveButton() method


/*
  doStartButton() method

  Do work as requested by the user: generating random passwords.  This program
  is actually very quick, but to allow the same code to be re-used, we do the
  work in a secondary thread, with a running status message at the bottom of
  the main window, and allow a "Cancel" button to interrupt the other thread.
*/
  static void doStartButton()
  {
    /* Get and check the user's options. */

    userAlphabet = alphabetDialog.getText(); // start with user's own text
    if (userAlphabet.length() == 0) // just in case user deleted all text
    {
      alphabetDialog.setText(DEFAULT_ALPHABET); // put default back into GUI
      alphabetDialog.select(0, 0); // scroll left if text field too small
      userAlphabet = DEFAULT_ALPHABET; // and proceed with default alphabet
    }

    try { userLength = Integer.parseInt((String) lengthDialog
      .getSelectedItem()); }
    catch (NumberFormatException nfe) { userLength = -1; }
    if ((userLength < LENGTH_LOWER) || (userLength > LENGTH_UPPER))
    {
      JOptionPane.showMessageDialog(mainFrame,
        "Length of each password must be from " + LENGTH_LOWER + " to "
        + LENGTH_UPPER + " characters.");
      lengthDialog.setSelectedItem(String.valueOf(LENGTH_DEFAULT));
      return;
    }

    try { userNumber = Integer.parseInt((String) numberDialog
      .getSelectedItem()); }
    catch (NumberFormatException nfe) { userNumber = -1; }
    if ((userNumber < NUMBER_LOWER) || (userNumber > NUMBER_UPPER))
    {
      JOptionPane.showMessageDialog(mainFrame,
        "Number of passwords to generate must be from " + NUMBER_LOWER + " to "
        + NUMBER_UPPER + ".");
      numberDialog.setSelectedItem(String.valueOf(NUMBER_DEFAULT));
      return;
    }

    /* We have our options.  Disable the "Start" button until we are done, and
    enable a "Cancel" button in case our secondary thread runs for a long time
    and the user panics. */

    cancelButton.setEnabled(true); // enable button to cancel this processing
    cancelFlag = false;           // but don't cancel unless user complains
    outputText.setText("");       // clear output text area
    startButton.setEnabled(false); // suspend "Start" button until we are done

    /* Clear status message (bottom of window) and start secondary thread. */

    setStatusMessage(EMPTY_STATUS); // clear text in status message
    statusTimer.start();          // start updating status on clock ticks

    doStartThread = new Thread(new RandomPassword3User(), "doStartRunner");
    doStartThread.setPriority(Thread.MIN_PRIORITY);
                                  // use low priority for heavy-duty workers
    doStartThread.start();        // run separate thread to do the real work

  } // end of doStartButton() method


/*
  doStartRunner() method

  This method is called inside a separate thread by the runnable interface of
  our "user" class to do the work as requested by the user in the context of
  the "main" class.  By doing all the heavy-duty work in a separate thread, we
  won't stall the main thread that runs the graphical interface, and we allow
  the user to cancel the processing if it takes too long.
*/
  static void doStartRunner()
  {
    /* Generate random passwords using options given to us by the user. */

    generatePasswords();          // call common routine, both console and GUI

    /* We are done.  Turn off the "Cancel" button and allow the user to click
    the "Start" button again. */

    cancelButton.setEnabled(false); // disable "Cancel" button
    startButton.setEnabled(true); // enable "Start" button
    statusTimer.stop();           // stop updating status message by timer
    setStatusMessage(EMPTY_STATUS); // and clear any previous status message

  } // end of doStartRunner() method


/*
  generatePasswords() method

  Generate random passwords.  All options must already be parsed, checked for
  proper values, and saved in our class variables.

  You may customize this method to generate passwords in other forms.  To avoid
  confusing the user, please remove or disable unused GUI elements.
*/
  static void generatePasswords()
  {
    StringBuffer buffer;          // faster than String for multiple appends
    int i, k;                     // index variables
    Random randomizer;            // random number generator
    int utf16Count;               // number of UTF-16 <char> in alphabet
    int[] utf32Alpha;             // alphabet array as UTF-32 characters
    int utf32Char;                // one UTF-32 character (code point)
    int utf32Count;               // total number of UTF-32 characters

    /* Java strings are encoded in UTF-16 format.  If we randomly select <char>
    from a user's string, an unmatched half may be taken from a surrogate pair.
    Convert the alphabet string to an integer array of UTF-32 characters.  Java
    5.0 or later has methods for this; Java 1.4 does not.  There are many other
    combining characters that won't display properly in isolation. */

    i = 0;                        // starting index into alphabet string
    utf16Count = userAlphabet.length(); // number of UTF-16 <char> elements
    utf32Alpha = new int[utf16Count]; // same size or more than we need
    utf32Count = 0;               // number of UTF-32 characters found
    while (i < utf16Count)        // do all <char> in alphabet string
    {
      utf32Char = (int) userAlphabet.charAt(i ++); // Java 1.4
//    utf32Char = userAlphabet.codePointAt(i); // Java 5.0 or later
//    i += Character.charCount(utf32Char); // Java 5.0 or later
      utf32Alpha[utf32Count ++] = utf32Char; // all versions
    }

    /* Generate random passwords from the array of UTF-32 characters. */

    buffer = new StringBuffer();  // allocate empty string buffer for result
    randomizer = new Random();    // get new generator based on system time
    for (i = 0; i < userNumber; i ++) // for each password requested
    {
      setStatusMessage("Generating password number " + (i + 1) + " of "
        + userNumber + "...");
      if (userDelay > 0) try { Thread.sleep(userDelay); } // sleep for effect
      catch (InterruptedException ie) { /* do nothing */ } // so work is hard
      buffer.setLength(0);        // throw away any previous string contents
      for (k = 0; k < userLength; k ++) // generate one random password
      {
        utf32Char = utf32Alpha[randomizer.nextInt(utf32Count)]; // random text
        buffer.append((char) utf32Char); // Java 1.4
//      buffer.appendCodePoint(utf32Char); // Java 5.0 or later
      }
      if (cancelFlag) break;      // exit <for> loop if cancel or fatal error
      putOutput(buffer.toString()); // show password to user
    }
  } // end of generatePasswords() method


/*
  putOutput() method

  Append a complete line of text to the end of the output text area.  We add a
  newline character at the end of the line, not the caller.  By forcing all
  output to go through this same method, one complete line at a time, the
  generated output is cleaner and can be redirected.

  The output text area is forced to scroll to the end, after the text line is
  written, by selecting character positions that are much too large (and which
  are allowed by the definition of the JTextComponent.select() method).  This
  is easier and faster than manipulating the scroll bars directly.  However, it
  does cancel any selection that the user might have made, for example, to copy
  text from the output area.
*/
  static void putOutput(String text)
  {
    if (mainFrame == null)        // during setup, there is no GUI window
      System.out.println(text);   // console output goes onto standard output
    else
    {
      outputText.append(text + "\n"); // graphical output goes into text area
      outputText.select(999999999, 999999999); // force scroll to end of text
    }
  }


/*
  setStatusMessage() method

  Set the text for the status message if we are running as a GUI application.
  This gives the user some indication of our progress when processing is slow.
  If the update timer is running, then this message will not appear until the
  timer kicks in.  This prevents the status from being updated too often, and
  hence being unreadable.
*/
  static void setStatusMessage(String text)
  {
    if (mainFrame == null)        // are we running as a console application?
      return;                     // yes, console doesn't show running status
    statusPending = text;         // always save caller's status message
    if (statusTimer.isRunning())  // are we updating on a timed basis?
      return;                     // yes, wait for the timer to do an update
    statusDialog.setText(statusPending); // show the status message now
  }


/*
  showHelp() method

  Show the help summary.  This is a UNIX standard and is expected for all
  console applications, even very simple ones.
*/
  static void showHelp()
  {
    System.err.println();
    System.err.println(PROGRAM_TITLE);
    System.err.println();
    System.err.println("  java  RandomPassword3  [options]  [numberOfPasswords]");
    System.err.println();
    System.err.println("Options:");
    System.err.println("  -? = -help = show summary of command-line syntax");
    System.err.println("  -a\"string\" = alphabet of available characters; default is alphanumeric");
    System.err.println("  -c# = number of characters in each password; default is -c" + LENGTH_DEFAULT);
    System.err.println("  -n# = initial value for number of passwords (GUI only); default is -n" + NUMBER_DEFAULT);
    System.err.println("  -t# = time delay between passwords in milliseconds; default is -t" + DELAY_DEFAULT);
    System.err.println("  -u# = font size for buttons, dialogs, etc; example: -u16");
    System.err.println("  -w(#,#,#,#) = normal window position: left, top, width, height;");
    System.err.println("      example: -w(50,50,700,500)");
    System.err.println("  -x = maximize application window; default is normal window");
    System.err.println();
    System.err.println("Console output may be redirected with the \">\" operator.  If no parameter is");
    System.err.println("given on the command line for the desired number of passwords, then a graphical");
    System.err.println("user interface (GUI) will open.  Note that -n# is an option, not a parameter.");
    System.err.println();
    System.err.println(COPYRIGHT_NOTICE);
//  System.err.println();

  } // end of showHelp() method


/*
  userButton() method

  This method is called by our action listener actionPerformed() to process
  buttons, in the context of the main RandomPassword3 class.
*/
  static void userButton(ActionEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == cancelButton)   // "Cancel" button
    {
      doCancelButton();           // stop secondary processing thread
    }
    else if (source == exitButton) // "Exit" button
    {
      System.exit(0);             // always exit with zero status from GUI
    }
    else if (source == fontNameDialog) // font name for output text area
    {
      /* The font name will be valid, because we obtained a list of names from
      getAvailableFontFamilyNames() and the dialog box is not editable. */

      outputFontName = (String) fontNameDialog.getSelectedItem();
      outputFont = new Font(outputFontName, Font.PLAIN, outputFontSize);
      outputText.setFont(outputFont);
    }
    else if (source == fontSizeDialog) // point size for output text area
    {
      /* The font size will be valid, since we provide a list of sizes and the
      dialog box is not editable. */

      outputFontSize = Integer.parseInt((String) fontSizeDialog
        .getSelectedItem());
      outputFont = new Font(outputFontName, Font.PLAIN, outputFontSize);
      outputText.setFont(outputFont);
    }
    else if (source == saveButton) // "Save Output" button
    {
      doSaveButton();             // write output text area to a file
    }
    else if (source == startButton) // "Start" button
    {
      doStartButton();            // start doing what it is that we do (joke)
    }
    else if (source == statusTimer) // update timer for status message text
    {
      if (statusDialog.getText().equals(statusPending) == false)
        statusDialog.setText(statusPending); // new status, update the display
    }
    else                          // fault in program logic, not by user
    {
      System.err.println("Error in userButton(): unknown ActionEvent: "
        + event);                 // should never happen, so write on console
    }
  } // end of userButton() method

} // end of RandomPassword3 class

// ------------------------------------------------------------------------- //

/*
  RandomPassword3User class

  This class listens to input from the user and passes back event parameters to
  a static method in the main class.
*/

class RandomPassword3User implements ActionListener, Runnable
{
  /* empty constructor */

  public RandomPassword3User() { }

  /* button listener, dialog boxes, etc */

  public void actionPerformed(ActionEvent event)
  {
    RandomPassword3.userButton(event);
  }

  /* separate heavy-duty processing thread */

  public void run()
  {
    RandomPassword3.doStartRunner();
  }

} // end of RandomPassword3User class

/* Copyright (c) 2024 by Keith Fenske.  Apache License or GNU GPL. */
