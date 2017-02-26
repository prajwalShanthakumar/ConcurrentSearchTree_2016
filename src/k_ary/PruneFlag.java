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
public class PruneFlag extends UpdateStep{
    public Node l = null;           // leaf to be deleted/shortened
    public Node p = null;           // parent (possibly going to be deleted by swining grandparent pointer)
    public Node gp = null;          // grandparent
    
    public UpdateStep ppending = null;   // check if another inserter/deleter is using the parent's links; if yes, don't swing over parent
    public int gpindex = 0;              // parent is which index of grand-parent?
    public int stamp_oldleaf;
    public int stamp_gpchild;
    public int stamp_parentflag;
    
    PruneFlag(Node gp, Node p, Node l, UpdateStep ppending, int gpindex, int stamp_oldleaf, int stamp_gpchild, int stamp_parentflag){
        this.gp = gp;
        this.p = p;
        this.l = l;
        this.ppending = ppending;
        this.gpindex = gpindex;
        this.stamp_oldleaf = stamp_oldleaf;
        this.stamp_gpchild = stamp_gpchild;
        this.stamp_parentflag = stamp_parentflag;
    }
}

