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
public class Mark extends UpdateStep{   // class used to mark a node as deleted
    public PruneFlag pending = null;   
    public int markStamp = 0;
    Mark(PruneFlag pending, int markStamp){
        this.pending = pending;
        this.markStamp = markStamp;
    }
}
