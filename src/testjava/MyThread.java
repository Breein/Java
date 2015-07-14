package testjava;

class MyThread implements Runnable{
    String name;
    Thread T;
    
    MyThread(String threadName) {
        name = threadName;
        T = new Thread(this, name);
        System.out.println("New Thread: " + T);
        T.start();
    }
    
    @Override
    public void run(){
        try{
            for(int i = 0; i < 5; i++){
                System.out.println(name + ": " + i);
                Thread.sleep(1000);
            }
        }catch(InterruptedException e){
            System.out.println(name + " - Will stoped!");
        }
        System.out.println(name + ", stoped.");
    }
    
}
