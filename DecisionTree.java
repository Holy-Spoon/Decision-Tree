// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2025T2, Assignment 4
 * Name: Matthew McGowan
 * Username: mcgowamatt
 * ID:300672872
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        UI.clearText();
        /*# YOUR CODE HERE */
        if (theTree == null) {
            UI.println("Tree is empty, try loading again");
            return;
        }
        printTreeHelper(theTree,"","");
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        /*# YOUR CODE HERE */
        if (theTree == null) {
            UI.println("Tree is empty, try loading again");
            return;
        }
        runTreeHelper(theTree);
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        /*# YOUR CODE HERE */
        if (theTree == null) {
            UI.println("Tree is empty, try loading again");
            return;
        }
        growTreeHelper(theTree);
    }
    
    public void saveTree() {
        String filename = UIFileChooser.save("Save tree to file");
        if (filename == null) { return; }
    
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            saveTreeHelper(theTree, out);
            out.close();
            UI.println("Tree saved to " + filename);
        } catch (IOException e) {
            UI.println("File writing failed: " + e);
        }
    }
    
    public void drawTree() {
        UI.clearGraphics();
        if (theTree != null) {
            drawTreeHelper(theTree, 50, 250, 100); 
        }
    }

    
    // You will need to define methods for the Completion and Challenge parts.
    private void printTreeHelper(DTNode node, String indent, String branchLabel) {
        if (node == null) return;
    
        // Decide how to print this node
        if (node.isAnswer()) {
            // it's a leaf (answer)
            UI.println(indent + branchLabel + " " + node.getText());
        } else {
            // it's a question
            UI.println(indent + branchLabel + " " + node.getText()+ "?");
    
            // recurse for children
            printTreeHelper(node.getYes(), indent + "  ", "y:");
            printTreeHelper(node.getNo(), indent + "  ", "n:");
        }
    }
    
    private void runTreeHelper(DTNode node){
        if(node.isAnswer()){
            UI.println("The anwser is: " + node.getText());
        } else {
            boolean answer = UI.askBoolean("Is it true: " + node.getText() + " (Y/N):" );
            if(answer){
                runTreeHelper(node.getYes());
            } else {
                runTreeHelper(node.getNo());
            }
        }
    }
    
    private void growTreeHelper(DTNode node){
        if(node.isAnswer()){
            boolean finalAnswer = UI.askBoolean("I think I know. Is it a " + node.getText() + "?");
            
            if(finalAnswer) {
                UI.println("Im so good at this :)");
            } else {
                String newAnswer = UI.askString("OK, what should the answer be?"); 
                String newQuestion = UI.askString("Oh, Whats the difference between a " + newAnswer +" and a " + node.getText());

                
                 // store the old wrong answer
                DTNode oldAnswer = new DTNode(node.getText());
                // make a new node for the correct answer
                DTNode correctAnswer = new DTNode(newAnswer);
                
                node.setText(newQuestion);
                
                boolean isYes = UI.askBoolean("For a " + newAnswer + ", is the answer to '" + newQuestion + "' yes?");
                if (isYes) {
                    node.setChildren(correctAnswer, oldAnswer);
                } else {
                    node.setChildren(oldAnswer, correctAnswer);
                }
                
                UI.println("Thank you! I've updated my decision tree.");
            }
            
        } else {
            boolean answer = UI.askBoolean("Is it true: " + node.getText() + " (Y/N):" );
            if(answer){
                growTreeHelper(node.getYes());
            } else {
                growTreeHelper(node.getNo());
            }
        }
    }
    
    private void saveTreeHelper(DTNode node, PrintWriter out) {
        if (node == null) return;
    
        if (node.isAnswer()) {
            out.println("Answer: " + node.getText());
        } else {
            out.println("Question: " + node.getText());
            saveTreeHelper(node.getYes(), out);
            saveTreeHelper(node.getNo(), out);
        }
    }
    
    private void drawTreeHelper(DTNode node, double x, double y, double yOffset) {
        if (node == null) return;
        
        // draw this node
        node.draw(x, y);
    
        double childX = x + 150;  // horizontal spacing to children
    
        // draw YES child above
        if (node.getYes() != null) {
            double childY = y - yOffset;
            UI.drawLine(x+DTNode.WIDTH/2, y, childX-DTNode.WIDTH/2, childY);
            drawTreeHelper(node.getYes(), childX, childY, yOffset/2);
        }
    
        // draw NO child below
        if (node.getNo() != null) {
            double childY = y + yOffset;
            UI.drawLine(x+DTNode.WIDTH/2, y, childX-DTNode.WIDTH/2, childY);
            drawTreeHelper(node.getNo(), childX, childY, yOffset/2);
        }
    }
    // Written for you

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }

}
