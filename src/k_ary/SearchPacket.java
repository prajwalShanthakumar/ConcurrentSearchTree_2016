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
public class SearchPacket {
    Node gparent;
    Node parent;
    Node leaf;
    UpdateStep gppending;
    UpdateStep ppending;
    int gpindex;
    int pindex;
    int stamp_pflag;
    int stamp_gpflag;
    int stamp_oldleaf;
    int stamp_gpchild;
    
    SearchPacket(Node gparent, Node parent, Node leaf, UpdateStep gppending, UpdateStep ppending, int gpindex, int pindex, int stamp_pflag, int stamp_gpflag, int stamp_oldleaf, int stamp_gpchild ){
        this.gparent = gparent;
        this.parent = parent;
        this.leaf = leaf;
        this.gppending = gppending;
        this.ppending = ppending;
        this.gpindex = gpindex;
        this.pindex = pindex;
        this.stamp_pflag = stamp_pflag;
        this.stamp_gpflag = stamp_gpflag;
        this.stamp_oldleaf = stamp_oldleaf;
        this.stamp_gpchild = stamp_gpchild;
    }
}
