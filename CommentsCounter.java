import java.util.*;
import java.lang.*;
import java.io.*;

/* Name of the class has to be "Main" only if the class is public. */
class CommentsCounter {
	public static void main (String[] args) {
		// Prompt for file name
		System.out.println("Enter file path:");
		Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        
        // Figure out file name and extension
        int slashPos = filePath.lastIndexOf("/");
        String fileName = slashPos == -1 ? filePath : filePath.substring(slashPos + 1);
		
		// Set extension to empty if file has no period or if first character is a period
		int dotPos = fileName.lastIndexOf(".");
		String fileExtension = dotPos == -1 ? "" : fileName.substring(dotPos + 1);
		
        if (!fileExtension.equals("") && !(fileName.length() > 0 && fileName.charAt(0) == '.')) {
            BufferedReader reader;
            int numLines = 0, commentLines = 0, singleLineComments = 0, linesWithinBlocks = 0, commentBlocks = 0, toDo = 0;
            try {
                reader = new BufferedReader(new FileReader(filePath));
                String line = reader.readLine();
                
                boolean inCommentBlock = false;
                
                // This will be true if the PREVIOUS LINE's first non-whitespace character is '#' (for Python comments)
                boolean possibleBlockComment = false;
                
                while (line != null) {
                    // Add 1 to lines of code   
                    numLines++;          
                    
                    // Detect if file is a python file through extension
                    if (fileExtension.equals("py")) {
                        boolean hasSeenNonWhiteSpace = false;
                        
                        // If empty string, "block" has been broken
                        if (line.length() == 0) {
                            inCommentBlock = false;
                            possibleBlockComment = false;
                        }
                        
                        for (int i = 0; i < line.length(); i++) {                          
                            // Comment Detected
                            if (line.charAt(i) == '#') {
                                
                                commentLines++;
                                // If has seen non-space character in same line, must be an in-line comment (single line comment)
                                if (hasSeenNonWhiteSpace) {
                                    singleLineComments++;
                                    inCommentBlock = false;
                                    possibleBlockComment = false;
                                // If in a comment block, still in a comment block if line starts with #
                                } else if (inCommentBlock) {
                                    linesWithinBlocks++;
                                // If previous line doesn't start with #, tentatively consider this a single line comment
                                } else if (!possibleBlockComment) {
                                    possibleBlockComment = true;
                                    singleLineComments++;
                                // Otherwise MUST be 2nd line of block comment, here we can correctly classify as block comment (and thus not a single line comment)
                                } else {
                                    singleLineComments--;
                                    commentBlocks++;
                                    linesWithinBlocks += 2;
                                    inCommentBlock = true;
                                }
                                
                                // Since rest of line must be a comment, simply search for TODO in rest of line
                                if (line.substring(i + 1).indexOf("TODO") != -1) {
                                    toDo++;
                                }
                                break;
                            } else if (!Character.isWhitespace(line.charAt(i))) {
                               hasSeenNonWhiteSpace = true;
                               inCommentBlock = false;
                               possibleBlockComment = false;
                            }
                        }
                    // Assume Java-Style Comments Otherwise
                    } else {        
                        boolean isSingleLineComment = false;
                        boolean isCommentBlockLine = inCommentBlock;
                        
                        // Loop through each character
                        for (int i = 0; i < line.length(); i++) {
                            // Detected block comment start, perform checks to make sure /* isn't commented out
                            if (i > 0 && line.charAt(i - 1) == '/' && line.charAt(i) == '*' && !inCommentBlock && !isSingleLineComment) {
                                inCommentBlock = true;
                                isCommentBlockLine = true;
                                commentBlocks++;
                                i++;
                            // Detected block comment end, perform checks to make sure */ isn't commented out
                            } else if (i > 0 && line.charAt(i - 1) == '*' && line.charAt(i) == '/' && inCommentBlock && !isSingleLineComment) {
                                inCommentBlock = false;
                                i++;
                            // Detected single line comment
                            } else if (i > 0 && line.charAt(i - 1) == '/' && line.charAt(i) == '/' && !inCommentBlock) {
                                isSingleLineComment = true;
                            // Detected TODO, must make sure that it is within a comment
                            } else if (i > 2 && line.charAt(i - 3) == 'T' && line.charAt(i - 2) == 'O' && line.charAt(i - 1) == 'D' && line.charAt(i) == 'O' && (inCommentBlock || isSingleLineComment)) {
                                toDo++;
                            }
                        }
                        if (isSingleLineComment || isCommentBlockLine) {
                            commentLines++;
                        }
                        if (isSingleLineComment) {
                            singleLineComments++;
                        }
                        if (isCommentBlockLine) {
                            linesWithinBlocks++;
                        }
                    }
                    
                    
                    
                    line = reader.readLine();
                }
            } catch (IOException e) {
                System.out.println("Error Reading File");
            }
            System.out.println("Total # of lines: " + numLines);
            System.out.println("Total # of comment lines: " + commentLines);
            System.out.println("Total # of single line comments: " + singleLineComments);
            System.out.println("Total # of comment lines within block comments: " + linesWithinBlocks);
            System.out.println("Total # of block line comments: " + commentBlocks);
            System.out.println("Total # of TODO’s: " + toDo);
        }
	}
}