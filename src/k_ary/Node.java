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
public class Node {
    
    public final static int K = 4;
    public int[] Key;
    
    Node(){
        
        Key = new int[K - 1];
        for(int i = 0; i < K - 1; i++){
            Key[i] = Integer.MAX_VALUE;
        }
    }
    
    boolean contains(int key){
        for(int i = 0; i < (K - 1); i++){
            if(key == Key[i])
                return true;
        }
            return false;
    }
    
    
}
