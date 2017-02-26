/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k_ary;

/**
 *
 * @author Prajwal
 */
public class ReplaceFlag extends UpdateStep{  // flag used during insertion; basically lock on parent and child
    public Node l = null;        // what's this? Perhaps the leaf will be extended?
    public Node p = null;        // parent of node that's going to be inserted?
    public Node newChild = null; // self-explanatory
    public int pindex = 0;       // the child is which index of parent? // which child pointer to swing basically...
    
    ReplaceFlag(){   
    }
    
    ReplaceFlag(Node l, Node p, Node newChild, int pindex){
        this.l = l;
        this.p = p;
        this.newChild = newChild;
        this.pindex = pindex;
    }
}
