/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k_ary;

import java.util.Random;

/**
 *
 * @author Prajwal
 */
public class K_ary {

    /**
     *
     * @param args the command line arguments
     */
    public static SearchTree st = new SearchTree();
    public static void main(String[] args) {
        // TODO code application logic here
        
        int num_threads = Integer.parseInt(args[0]);  // number of threads
        int total_ops = Integer.parseInt(args[1]);  // total number of operations
        int contention_parameter = Integer.parseInt(args[2]);  // upper limit on key range
        int inserts = Integer.parseInt(args[3]); // num_inserts
        int deletes = Integer.parseInt(args[4]); // num_deletes
        int contains = Integer.parseInt(args[5]);  // num_contains
       /* st.Insert(10);
        st.Insert(15);
        st.Insert(20);
        st.Delete(10);
        System.out.println("delete 10 result:" + st.Delete(10));
        System.out.println(st.Insert(25));
        System.out.println(st.Insert(17));
        System.out.println(st.Insert(12));
        System.out.println(st.Insert(10));*/
       /* System.out.println(st.Insert(-3));
        System.out.println(st.Insert(30));
        System.out.println(st.Insert(50));*/
        
        /*System.out.println(st.Find(3));
        System.out.println(st.Find(10));
        System.out.println(st.Find(2));
        System.out.println(st.Find(0));*/
        Test t = new Test(num_threads,total_ops,contention_parameter,inserts,deletes,contains);
        t.myTest();
        

    }
    
    
    
}
