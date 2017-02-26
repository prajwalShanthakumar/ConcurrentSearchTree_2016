/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k_ary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 *
 * @author Prajwal
 */
public class SearchTree {
    public Internal root;
    private ThreadLocal internalNode_pool;
    private ThreadLocal leafNode_pool;
    
    
    SearchTree(){
        this.leafNode_pool = new ThreadLocal<ArrayList<Leaf>>() {
            @Override protected ArrayList<Leaf> initialValue() {
                return new ArrayList<Leaf>();
            }
        };
        
        this.internalNode_pool = new ThreadLocal<ArrayList<Internal>>(){
            @Override protected ArrayList<Internal> initialValue() {
                return new ArrayList<Internal>();
            }
        };
       
       //ArrayList<Leaf> myLeafPool = new ArrayList<Leaf>(2);
       //myLeafPool.add(new Leaf());
       //leafNode_pool.set(myLeafPool); 
       
       
       root =  new Internal();                  // point root to new internal node
       root.pending.set(new Clean(), 0);                                 // clean flag initially
       
       Internal root_fc = new Internal(); 
       root_fc.pending.set(new Clean(),0);      
       //System.out.println(root.c.size());
       
       root.c.get(0).set(root_fc, 0);  // child 0 pointer is pointing to root_fc
       
       for(int i = 1; i < Node.K; i++){   // k - 1 leaves
            root.c.get(i).set(new Leaf(), 0);       // REVISIT TIMESTAMPS
       }
       
       for(int i = 0; i < Node.K; i++){   // k leaves
            root_fc.c.get(i).set(new Leaf(), 0);
       }
    }
    
    boolean Find(int key){
    while(true){
      SearchPacket packet = Search(key);//  if Leaf returned by Search(key) contains key, then return True, else return False 
      boolean res;
        if(packet.leaf == null)
            res = false;
        else
            res = packet.leaf.contains(key);
   
            if(packet.stamp_oldleaf == ((Internal)packet.parent).c.get(packet.pindex).getStamp())
                return res;
    }
    }
    
    SearchPacket Search(int key){
        Node gparent = root;
        Node parent = root;
        int[] stamp_pflag = new int[1];
        int[] stamp_gpflag = new int[1];
        int[] stamp_oldleaf = new int[1];
        int[] stamp_gp_child = new int[1];
 
        Node leaf = (Node)((Internal)parent).c.get(0).get(stamp_oldleaf);
        
        
        UpdateStep gppending = ((Internal)parent).pending.get(stamp_gpflag);
        UpdateStep ppending = ((Internal)parent).pending.get(stamp_pflag);
        
        int gpindex = 0;
        int pindex = 0;
        
        while(leaf instanceof Internal){    // repeat till leaf is no longer an internal and is actually a leaf; then return results of your search
            //stamp_gp_child[0] = ((Internal)parent).c.get(pindex).getStamp();
            gparent = parent;
            gppending = ppending;       // advance grandparent                              //PPPPP
            gpindex = pindex;           // advance grandparent index
            stamp_gpflag[0] = stamp_pflag[0];
            stamp_gp_child[0] = stamp_oldleaf[0];
            
            
            parent = leaf;
            ppending = ((Internal)parent).pending.get(stamp_pflag); //advance parent
            

            int tempIndex = 0;
            Node tempLeaf = null;
            for(int i = 0; i < (Node.K - 1); i++){    // COULD MAKE THIS MORE EFFICIENT BY TRAVERSING ONLY IF NO DUPLICATE?
                
                
                if(key < parent.Key[i]){
                    
                    tempLeaf = ((Internal)parent).c.get(i).get(stamp_oldleaf);
                    tempIndex = i;
                    break;
                }
                else if(i == ((Node.K - 1) - 1)) {   // key is greater than all keys of parent
                      // i + 1 = (Node.K - 1)
                    tempLeaf = ((Internal)parent).c.get(i+1).get(stamp_oldleaf);
                    tempIndex = i + 1;
                }        
            } 
            leaf = tempLeaf;    // advance leaf
            
            //System.out.println(leaf.Key[0]);
            pindex = tempIndex;           // leaf is which child of parent... Update that also
                
        }
        SearchPacket packet = new SearchPacket(gparent,parent,leaf,gppending,ppending,gpindex,pindex,stamp_pflag[0],stamp_gpflag[0],stamp_oldleaf[0], stamp_gp_child[0]);
        //System.out.println(gpindex);
        //System.out.println(pindex);
        
        return packet;
    }
    
    boolean Insert(int key){
        Node p;
        Node newChild;
        Leaf l;
        UpdateStep ppending;
        int pindex;
        
        while(true){
            //System.out.println("stuck");
            SearchPacket packet = Search(key);
            
            if(packet.leaf.contains(key)){
                if(packet.stamp_oldleaf == ((Internal)packet.parent).c.get(packet.pindex).getStamp())
                    return false;
            }
            
            else{
                
            
            p = packet.parent;
            l = (Leaf)packet.leaf;                // derived_ptr to base_ptr to derived_ptr may wreak havoc???
            ppending = packet.ppending;
            
            pindex = packet.pindex;
            
            if(!(ppending instanceof Clean)){    // if the parent flag is not clean, HELP the operation that is underway and take it from the TOP
                /*if(ppending instanceof ReplaceFlag)
                    System.out.println("replace flag");
                else if(ppending instanceof PruneFlag)
                    System.out.println("prune flag");
                else if(ppending instanceof Mark)
                    System.out.println("mark");*/
                
                Help(ppending,packet.stamp_pflag);
            }
            
            else {                              // INSERTING
                //System.out.print("else");
                int leaf_keyCount = l.keyCount;
                if(l.keyCount == Node.K - 1){     //  SPROUTING insertion (if leaf node is already full)
                    //Leaf oldleaf = l;
                    
               /////////////////////////////////////////////////  CREATE NEW CHILD AND ASSIGN OLD KEYS  (LOCAL OPERATIONS)
                    int[] tempKeys = new int[Node.K];
                    for(int i = 0; i < (Node.K - 1); i++){        // adding old keys to temp array
                        tempKeys[i] = l.Key[i];
                    }
                    tempKeys[(Node.K - 1)] = key;                 // adding new key to temp array
                    
                    Arrays.sort(tempKeys);                              // sorting temp array to consider only the k-1 largest keys
                    
                    //Internal newInternal;
                    if( ((ArrayList<Internal>)internalNode_pool.get()).size() != 0 ){
                        newChild = ((ArrayList<Internal>)internalNode_pool.get()).remove(0);
                        ((Internal)newChild).reuse();
                        //System.out.println("reusing internal insert " + key);
                        //((Internal)newChild).c.get(0).set(newleaf,(((Internal)newChild).c.get(0)).getStamp()+1 );
                    }
                    else{
                        newChild = new Internal();
                    }
                    //((Internal)newChild).pending.set(new Clean());
                    ((Internal)newChild).pending.set(new Clean(),0);
                    
                    for(int i = 0; i < (Node.K - 1); i++){        // adding the k-1 largest keys to newChild
                        newChild.Key[i] = tempKeys[i+1];
                    }                                                       // STEP 1 complete; now have to create and add the actual leaf nodes 
                    
                    //((Internal)newChild).c[0] = new Leaf(tempKeys[0]);                // smallest key is the leftmost child
                    Leaf newleaf;
                    
                    if( ((ArrayList<Leaf>)leafNode_pool.get()).size() != 0 ){
                        newleaf = ((ArrayList<Leaf>)leafNode_pool.get()).remove(0);
                        ((Leaf)newleaf).reuse((tempKeys[0]));
                        //System.out.println("reusing leaf insert " + key);
                        ((Internal)newChild).c.get(0).set(newleaf,(((Internal)newChild).c.get(0)).getStamp()+1 );
                    }
                    
                    else{
                        ((Internal)newChild).c.get(0).set(new Leaf(tempKeys[0]),(((Internal)newChild).c.get(0)).getStamp()+1);     //REVISIT TIMESTAMP
                    }
                   
                    for(int i = 1; i < Node.K; i++){                         // creating and adding links to k-1 right children
                        //((Internal)newChild).c[i] = new Leaf(tempKeys[i]);
                        if( ((ArrayList<Leaf>)leafNode_pool.get()).size() != 0 ){
                            newleaf = ((ArrayList<Leaf>)leafNode_pool.get()).remove(0);
                            ((Leaf)newleaf).reuse((tempKeys[i]));
                            //System.out.println("reusing leaf insert " + key);
                            /*if(newleaf == null)
                                System.out.println("alarm alarm!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");*/
                            ((Internal)newChild).c.get(i).set(newleaf,(((Internal)newChild).c.get(i)).getStamp()+1 );
                        }
                        else{
                            ((Internal)newChild).c.get(i).set(new Leaf(tempKeys[i]),(((Internal)newChild).c.get(i)).getStamp()+1);
                        }
                    }
                    
                 /////////////////////////////////////////////////////////////////////////// 

                }

                else{                                   // SIMPLE insertion (else)
                    
                    /////////////////////////////////////////////////  CREATE NEW CHILD AND ASSIGN OLD KEYS (LOCAL OPERATIONS)
                    if( ((ArrayList<Leaf>)leafNode_pool.get()).size() != 0 ){
                        newChild = ((ArrayList<Leaf>)leafNode_pool.get()).remove(0);
                        ((Leaf)newChild).reuse();
                        //System.out.println("reusing leaf");
                    }
                    else{   
                        newChild = new Leaf();
                    }
                    //System.arraycopy(l.Key, 0, newChild.Key, 0, (Node.K - 1) - 1); // adding already existing keys to newChild
                    leaf_keyCount = Math.min(leaf_keyCount, Node.K - 2);
                    for(int i = 0; i < leaf_keyCount; i++){    // adding already existing keys to newChild
                        newChild.Key[i] = l.Key[i];
                    }
                    //newChild.Key[(Node.K - 1) - 1] = key;         // adding new key to newChild
                    newChild.Key[leaf_keyCount] = key;
                    ((Leaf)newChild).keyCount = leaf_keyCount + 1;
                    
                    Arrays.sort(newChild.Key);                          // sorting all the keys  
                    /////////////////////////////////////////////////   
                }
                  
                
                /////////////////////////// KEY STEPS WILL NOW FOLLOW. 
                                            //1. FLAG THE PARENT
                                            //2. SWING POINTER FROM OLD LEAF TO NEW CHILD
                
                ///// newChild ready at this point; either thanks to a simple insertion or a sprouting insertion
                ReplaceFlag op = new ReplaceFlag(l,p,newChild,pindex);
               // boolean resultFlagCAS = ((Internal)p).pending.compareAndSet(ppending, op); // compare (old) parent's flag to clean (make sure nobody else if working on parent); if clean, set it to flagged.
                // we are still inside the insert block                                     // if a new parent replaces old parent but the new parent's flag points to the same clean object, we will end up operating on old parent even though situation has changed. ABA?
                boolean resultFlagCAS = ((Internal)p).pending.compareAndSet(ppending, op, packet.stamp_pflag, packet.stamp_pflag+1);
                if(resultFlagCAS){
                    //HelpReplace(op);
                    HelpReplace(op, packet.stamp_pflag+1,packet.stamp_oldleaf);
                    return true;
                }
                else{   // CAS failed; you got raced; help that operation and take it from the top
                    int[] tempStamp = new int[1];
                    UpdateStep temp = ((Internal)p).pending.get(tempStamp);
                    Help( temp, tempStamp[0]);  // THIS SHOULD BE FINE, BUT REVISIT
                    // at this point, either the other thread has not woken up, or it has woken up and swung child pointer
                    //HelpReplace( p.pending(replaceFlag), 
                }
            }
            }    
        }
    }
    
    boolean Delete(int key){
        Node gp,p;
        UpdateStep gppending, ppending;
        Leaf l;
        int pindex, gpindex;
        
        while(true){
            SearchPacket packet = Search(key);
            
            if(!(packet.leaf.contains(key))){    // if leaf does not contain key, return false (can't delete)
                if(packet.stamp_oldleaf == ((Internal)packet.parent).c.get(packet.pindex).getStamp())
                    return false;
            }
            
            else{
            gp = packet.gparent;
            p = packet.parent;
            l = (Leaf)packet.leaf;                // derived_ptr to base_ptr to derived_ptr may wreak havoc???
            gppending = packet.gppending;
            ppending = packet.ppending;
            pindex = packet.pindex;
            gpindex = packet.gpindex;
            
            if(!(gppending instanceof Clean)){    // if the grandparent flag is not clean, HELP the operation that is underway on that node and then take it from the TOP
                Help(gppending,packet.stamp_gpflag);
            }
            else if(!(ppending instanceof Clean)){    // if the parent flag is not clean, HELP the operation that is underway and take it from the TOP
                Help(ppending,packet.stamp_pflag);
            }
            else{                                      // DELETE
                int ccount = 0;
                for(int i = 0; i < Node.K; i++){                   // calculate the number of non-empty children of parent
                    //Node templeaf = (Node)((Internal)p).c.get(i);
                    //if( ((Leaf)templeaf).keyCount != 0){
                    Node child = ((Internal)p).c.get(i).getReference();
                    //System.out.println("i " + i + " ref " + child + " key " + child.Key[i]);
                    //if(((Internal)p).c.get(i).getReference() instanceof Internal)          // if a child has its own children, it's non-empty
                    if(child instanceof Internal)   
                        ccount++;
                    //else if(((Leaf)((Internal)p).c.get(i).getReference()).keyCount != 0)   // else if a child is a leaf and it has non-zero keys, it's non-empty
                    else if(((Leaf)child).keyCount != 0)    
                        ccount++;
                    
                }
                
                if((ccount == 2) && (l.keyCount == 1) /*&& p != root*/){  // PRUNING DELETION
                    PruneFlag op = new PruneFlag(gp, p, l, ppending, gpindex, packet.stamp_oldleaf, packet.stamp_gpchild, packet.stamp_pflag);  // flag is augmented with all this data to facilitate help from other threads
                    boolean result =  ((Internal)gp).pending.compareAndSet(gppending, op, packet.stamp_gpflag, packet.stamp_gpflag + 1);    // point the gp's pending reference to a pruneflag as opposed to a clean flag; acquiring lock                                      // try to acquire lock on grandparent
                    if(result){ // successful CAS
                        //System.out.println("prune prune!");
                        if(HelpPrune(op, packet.stamp_gpflag + 1, packet.stamp_pflag)){  // attempt to use parent
                            return true;
                        }
                    }
                    else{       // failed CAS; 
                        int[] tempStamp = new int[1];
                        UpdateStep temp = ((Internal)gp).pending.get(tempStamp);
                        Help( temp,tempStamp[0]);// help operation ongoing on gp node //OH OH
                    }
                }// end of pruning deletion
                
                else{                                   // SIMPLE deletion
                    Node newChild;
                    if( ((ArrayList<Leaf>)leafNode_pool.get()).size() != 0 ){
                        newChild = ((ArrayList<Leaf>)leafNode_pool.get()).remove(0);
                        ((Leaf)newChild).reuse();
                        //System.out.println("reusing leaf delete");
                    }
                    else{   
                        newChild = new Leaf();
                    }
                    
                    int idx = 0;
                    for(int i = 0; i < Node.K - 1; i++){
                        if(l.Key[i] != key){                     // as long as key is not the one to be deleted, move it to newChild
                            newChild.Key[idx] = l.Key[i];
                            idx++;
                            ((Leaf)newChild).keyCount++;
                        }   
                    }
                    
                    // new Child ready at this point; we can do a CAS and swing parent pointer from old leaf to new Child
                    ReplaceFlag op = new ReplaceFlag(l,p,newChild,pindex);
                    boolean resultFlagCAS = ((Internal)p).pending.compareAndSet(ppending, op, packet.stamp_pflag, packet.stamp_pflag + 1); 
                    
                    if(resultFlagCAS){
                        HelpReplace(op, packet.stamp_pflag + 1, packet.stamp_oldleaf);
                        return true;
                    }
                    else{   // CAS failed; you got raced; help that operation and take it from the top
                        //Help( ((Internal)p).pending.getReference(), ((Internal)p).pending.getStamp());
                        int[] tempStamp = new int[1];
                        UpdateStep temp = ((Internal)p).pending.get(tempStamp);
                        Help(temp,tempStamp[0]);
                    }
                }// end of simple deletion
                    
            }
            }

           
        }
        

    }
    
    void Help(UpdateStep op, int flagstamp ){
        if(op instanceof ReplaceFlag){
            int child = ((ReplaceFlag) op).pindex;
            int stamp_oldleaf = ((Internal)((ReplaceFlag) op).p).c.get(child).getStamp();
             //System.out.println("replace");
            HelpReplace((ReplaceFlag)op,flagstamp,stamp_oldleaf);
        }
        else if(op instanceof PruneFlag){
            //int child = ((PruneFlag) op).gpindex;
            //int stamp_oldleaf = ((Internal)((PruneFlag) op).gp).c.get(child).getStamp();
            
            HelpPrune((PruneFlag)op,flagstamp,((PruneFlag) op).stamp_parentflag);       // check this;
             //System.out.println("prune!");
        }
        else if(op instanceof Mark){
            //System.out.println("mark");
            HelpMarked(((Mark)op).pending, flagstamp, ((Mark) op).markStamp);    // also this;
            //HelpMarked(((Mark)op)
            //HelpMarked((PruneFlag)op);
            
        }
            
                    

    }
    // if we do a first CAS, then the originial thread wakes up and does the 2nd CAS, stamp will change and we will fail the second CAS
    // if we setup stamp, then originial thread wakes up and does both, stamp will change and we will fail the second CAS
    // if original thread wakes up, finishes second CAS, then we setup stamp, stamp will be the same but flag will be clean??????? When you reuse a replaceflag, increment it's stamp by 1.
    void HelpReplace(ReplaceFlag op, int stampAfterFlag, int stamp_oldleaf){    // swing parent pointer from old leaf to new child
        
        Leaf oldleaf = (Leaf)op.l;
        boolean CAS_result = ((Internal)op.p).c.get(op.pindex).compareAndSet(op.l, op.newChild,stamp_oldleaf,stamp_oldleaf+1);         // this is a CAS so that contains doesn't see weird things  // HELLO!
        if(CAS_result)
        {
            ((ArrayList<Leaf>)leafNode_pool.get()).add(oldleaf);
            //System.out.println(((ArrayList<Leaf>)leafNode_pool.get()).size());
        }
            
        //System.out.println( ((ArrayList<Leaf>)leafNode_pool.get()).size());
        //((Internal)op.p).pending.compareAndSet(op, new Clean(), ((Internal)op.p).pending.getStamp(), ((Internal)op.p).pending.getStamp()+1);                // this is a CAS so that you don't repeat the action if somebody else did it for you
        ((Internal)op.p).pending.compareAndSet(op, new Clean(), stampAfterFlag, stampAfterFlag + 1);
    }
    
    boolean HelpPrune(PruneFlag op, int stampAfterFlag, int stamp_pFlag){
        //boolean result = ((Internal)op.p).pending.compareAndSet(op.ppending, new Mark(op,((Internal)op.gp).c.get(op.gpindex).getStamp()), stamp_oldleaf, stamp_oldleaf+1); 
        boolean result = ((Internal)op.p).pending.compareAndSet(op.ppending, new Mark(op,op.stamp_gpchild), stamp_pFlag, stamp_pFlag+1); 
        int[] newStamp = new int[1];
        UpdateStep newValue = ((Internal)op.p).pending.get(newStamp);               // value of the just marked parent
        
        //((Internal)newValue)
        //if(  (result == true) || ((newValue instanceof Mark) && (((Mark)newValue).pending == op))  ) {  // UNSURE ABOUT THIS!!! ; if you or some other thread has marked the node for deletion, finish the job; this must be LP???
        if(result == true) {                            // successful CAS; help yourself
            //HelpMarked(op,stampBeforeMark+1);
            // stamp of the grandparent after flag but before parent is marked is 
            //((Internal)op.gp).c.get(op.gpindex).getStamp();
            HelpMarked(op,stampAfterFlag, op.stamp_gpchild);
            //System.out.println("help yourself");
            //HelpMarked(op,stampAfterFlag,((Internal)op.gp).c.get(op.gpindex).getStamp()+1)
            return true;    // 
        }
        else if(newValue instanceof Mark){              // failed CAS, someone marked your operation before you; finish the job
            if(((Mark)newValue).pending == op){
                
                //HelpMarked(op,stampAfterFlag, ((Mark)((Internal)op.p).pending.getReference()).markStamp);             // THIS SHOULD BE OK, BUT REVISIT
                //HelpMarked(op,stampAfterFlag,stamp_oldleaf+1)
                HelpMarked(op,stampAfterFlag,op.stamp_gpchild);
                //System.out.println("help other");
                return true;
                
            }
        }
        //else{
            //System.out.println("here!");
            Help(newValue,newStamp[0]); // help operation pending on parent; Rename variable newParentPending???
             ((Internal)op.gp).pending.compareAndSet(op, new Clean(),stampAfterFlag,stampAfterFlag+1) ; // let go of lock on gp and retry from the top
            return false;
        //}
       
    }
    
    void HelpMarked(PruneFlag op, int stampAfterFlag, int stamp_gp_child){
        Node other = null;                              // find the sibling you have to swing the grandparent child pointer to
        
        for(int i = 0; i < Node.K; i++){
            //if(  (((Internal)op.p).c.get(i) != op.l) && (((Leaf)((Internal)op.p).c.get(i)).keyCount != 0) ){           // find the child that is not the leaf to be deleted but the only other node which is non-empty
            Node prospective_sibling = ((Internal)op.p).c.get(i).getReference();
            if(  prospective_sibling != op.l ){ 
                /*if(prospective_sibling instanceof Internal)
                    System.out.print("prospective ");*/
                if( prospective_sibling instanceof Internal ){                     // if a child has it's own children, it's the sibling of type Internal
                    other = prospective_sibling;
                    break;
                }
                else if(prospective_sibling != null){
                    if(((Leaf)prospective_sibling).keyCount != 0){                // else if a child had non-zero number of keys, it's the other
                        other = prospective_sibling;
                        break;
                    }
                }
            }
        }
        if(other == null)
            other = ((Internal)op.p).c.get(0).getReference();
        
        
        boolean cas = ((Internal)op.gp).c.get(op.gpindex).compareAndSet(op.p, other,stamp_gp_child,stamp_gp_child+1);// swing grandparent's child pointer from parent to other //HELLO!
        //System.out.println("cas "+ cas);
        if(cas){
            ((ArrayList<Internal>)internalNode_pool.get()).add((Internal)op.p);
            ((ArrayList<Leaf>)leafNode_pool.get()).add((Leaf)op.l);
        }
        //System.out.println("stamp " + ((Internal)op.gp).c.get(op.gpindex).getStamp());
        ((Internal)op.gp).pending.compareAndSet(op, new Clean(),stampAfterFlag,stampAfterFlag+1 );              // release lock on grandparent node
    }
        


    
    
}
