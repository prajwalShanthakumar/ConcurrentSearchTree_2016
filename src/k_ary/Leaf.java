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
public class Leaf extends Node{
    public int keyCount = 0; // number of siblings?
    Leaf(int key){
        keyCount = 1;
        this.Key[0] = key;
    }
    Leaf(){
        
    }
    void reuse(int key){
        keyCount = 1;
        this.Key[0] = key;   
    }
    void reuse(){
        keyCount = 0;  
    }
}
