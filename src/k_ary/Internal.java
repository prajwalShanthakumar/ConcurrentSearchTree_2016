/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k_ary;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 *
 * @author Prajwal
 */
public class Internal extends Node{
    //public AtomicReferenceArray<Node> c = new AtomicReferenceArray<Node>(Node.K);
    
    //public AtomicReference<Node>[] c = (AtomicReference<Node>[])new Object[Node.K];
    public ArrayList<AtomicStampedReference<Node>> c = new ArrayList<>(Node.K); 
    
    //c = new Atomic
    //c = new AtomicReference<>[Node.K];
    //public AtomicReference<UpdateStep> pending = new AtomicReference<UpdateStep>();                                 // MAY HAVE TO BE INITIALIZED!!!
    public AtomicStampedReference<UpdateStep> pending = new AtomicStampedReference<UpdateStep>(null,0); 
    
    Internal()
    {
        for(int i = 0; i<Node.K;i++)
            c.add(new AtomicStampedReference<Node>(null,0));        //DEF_REVISIT
    }
    void reuse(){
        for(int i = 0; i<Node.K;i++){
            //c.get(i).set(null, c.get(i).getStamp() + 1);   
            c.get(i).set(c.get(i).getReference(), c.get(i).getStamp() + 1);// increment timestamp to avoid ABA
            
        }
        for(int i = 0; i<Node.K - 1;i++){
            this.Key[i] = 0;                            // PROBLEM???!!!
        }
        
    }
}
