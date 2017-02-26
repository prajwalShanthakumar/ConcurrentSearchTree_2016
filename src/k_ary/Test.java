/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k_ary;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prajwal
 */
public class Test {
    
    SearchTree st = new SearchTree();
    int thread_count;
    int total_ops;
    int key_range_upper_limit;
    int inserts;
    int deletes;
    int contains;
    
    Test(int thread_count, int total_ops, int key_range_upper_limit, int inserts, int deletes, int contains){
        this.thread_count = thread_count;
        this.total_ops = total_ops;
        this.key_range_upper_limit = key_range_upper_limit;
        this.inserts = inserts;
        this.deletes = deletes;
        this.contains = contains;
    }
        
    
    void myTest(){
        Thread[] myThreads = new Thread[thread_count]; // array of 3 base pointers to thread class
        for(int i = 0; i < thread_count; i++)
            myThreads[i] = new Thread_add_del(i);
        
        //myThreads[0] = new Thread_add_del(0);                                    // now pointing them to actual thread objects
        //myThreads[1] = new Thread_add_del(1);
        //myThreads[2] = new Thread_add_del(2);
        
        //for(int i = 0; i < thread_count; i++)
           // myThreads[i].start();
        
      long start_t = System.currentTimeMillis();
      
      for (int i = 0; i < thread_count; i++) {  // start all threads
      myThreads[i].start();
    }
      for (int i = 0; i < thread_count; i++) {    try {
          // wait for all threads to finish
          myThreads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
      
      System.out.println("run time = " + (System.currentTimeMillis() - start_t));
        
        
        //myThreads[2].start();
    }
    
    
    
    class Thread_add_del extends Thread {
    int start;
    Thread_add_del(int i) {
      start = i;
    }
    public void run() {
      Random rand = new Random();
      for (int i = 0; i < total_ops/100/thread_count; i++) {
          
          for(int j = 0; j < inserts; j++)
            st.Insert(rand.nextInt(key_range_upper_limit));
          
          for(int j = 0; j < contains; j++)
            st.Find(rand.nextInt(key_range_upper_limit));
          
          for(int j = 0; j < deletes; j++)
            st.Delete(rand.nextInt(key_range_upper_limit));
          
      }
      /*for (int i = 1; i < ops_per_thread; i++) {    //insert 0,3,6,9... or 1,4,7,10...
          //if(!st.Insert(i*3+start))
          //st.Insert(i*3+start);
            System.out.println("add " + (i*3 + start) + " " + st.Insert(i*3+start));
          //if(!st.Find(i*3+start))
          //st.Find(i*3+start);
          System.out.println("find " + (i*3 + start) + " " + st.Find(i*3+start));
          //if(!st.Delete(i*3+start))
          //st.Delete(i*3+start);
              System.out.println("delete " + (i*3 + start) + " " + st.Delete(i*3+start));
          //System.out.println("delete " + (i*3 + start+1) + " " + st.Delete(i*3+start+1));
        //instance.add(value + i);
        //instance.add(rand.nextInt(MAX_INT));
      }
      /*for (int i = 1; i < 30; i++) { 
          System.out.println("find " + (i*3 + start) + " " + st.Find(i*3+start));
        //instance.add(value + i);
        //instance.add(rand.nextInt(MAX_INT));
      }
      for (int i = 1; i < 30; i++) { 
          System.out.println("delete " + (i*3 + start) + " " + st.Delete(i*3+start));
        //instance.add(value + i);
        //instance.add(rand.nextInt(MAX_INT));
      }*/
    }
  }
}
