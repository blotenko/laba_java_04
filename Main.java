import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String args[]) {
        Q q = new Q();
        WriterToFile w1 = new WriterToFile(q,1);
        WriterToFile w2 = new WriterToFile(q,2);
        ReaderToConsole w3 = new ReaderToConsole(q,3);
        ReaderToConsole w4 = new ReaderToConsole(q,4);
       PolitZcvetok r1 = new PolitZcvetok(q,1);
        PolitZcvetok r2 = new PolitZcvetok(q,2);
        Priroda r3 = new Priroda(q,3);
        Priroda r4 = new Priroda(q,4);
        try {Thread.sleep(1000);}     catch(InterruptedException e) {};
        q.work = false;
        try {Thread.sleep(500);}     catch(InterruptedException e) {} ;
        System.out.println(
                "Писатель 1 выполнился: "+w1.i+" раз\n" +
                        "Писатель 2 выполнился: "+w2.i+" раз\n" +
                        "Писатель 3 выполнился: "+w3.i+" раз\n" +
                        "Писатель 4 выполнился: "+w4.i+" раз\n" +
                        "Читатель 1 выполнился: "+r1.i+" раз\n" +
                        "Читатель 2 выполнился: "+r2.i+" раз\n" +
                        "Читатель 3 выполнился: "+r3.i+" раз\n" +
                        "Читатель 4 выполнился: "+r4.i+" раз\n"
        );
    }
}



class Zvetok{

    // 0 - зав`ялий / 1 - нормальний

    String name;
    String stan;

    Zvetok(String n, String s){
        this.name = n;
        this.stan = s;
    }

    void display(){
        System.out.print(name);
        System.out.print(" ");
        if(stan == "0")  System.out.println("Vialiy");
        else  System.out.println("Sveshiy");
    }

}


class Q{
    static ArrayList<Zvetok> sad = new ArrayList<>();
    boolean work = true;
    Semaphore ReaderCanStart=new Semaphore(0,true);
    Semaphore WriterCanStart=new Semaphore(0,true);
    Semaphore PolitCanStart = new Semaphore(0,true);
    Semaphore PrirodaCanStart = new Semaphore(0,true);
    int WaitingReader = 0; int RunningReader = 0;
    int WaitingWriter = 0; int RunningWriter = 0;
    int WaitingPolit  = 0; int RinningPolit  = 0;
    int WaitingPriroda = 0; int RunningPriroda = 0;
    int readD() throws IOException {
        Thread t = Thread.currentThread();
        System.out.println(t.getName()+" try...");
        StartRead();
        System.out.println(t.getName()+" reading...");
        try { Thread.sleep(50); } catch (InterruptedException e) {}


        Vector<String> vec = new Vector<>();
        InputStream input = new FileInputStream("outputBazaFile.txt");
        StringBuilder builder = new StringBuilder();
        while(true){
            int data = input.read();
            if(data == -1) break;
            else{
                if(" ".charAt(0) == (char)data){
                    continue;
                }
                if ( ",".charAt(0) == (char)data){
                    vec.add(builder.toString());
                    builder.delete(0,builder.length());
                    continue;
                }
                if("-".charAt(0) == (char)data){
                    vec.add(builder.toString());
                    builder.delete(0,builder.length());
                    continue;
                }
                builder.append((char)data);
            }
        }
        vec.add(builder.toString());
        ArrayList<Zvetok> baz = new  ArrayList<>();
        for(int i =0; i<vec.size()-1; i++){
            baz.add(new Zvetok(vec.get(i), vec.get(i + 1)));
            i+=1;
        }
        sad = baz;
        for(int i =0; i<sad.size(); i++){
            sad.get(i).display();
        }
        StopRead();
        return 0;
    }

    void StartRead()
    {
        if ((RunningWriter>0) )
        {
            WaitingReader++;
            try {ReaderCanStart.acquire();} catch(InterruptedException e){}
        }
        else RunningReader++;
    }

    void StopRead()

    {
        RunningReader--;
         if((RunningReader==0) & (WaitingPolit>0)){
            WaitingPolit--;
            RinningPolit++;
            PolitCanStart.release();
        }
         else if ((RunningReader==0) & (WaitingWriter>0))
         {
             WaitingWriter--;
             RunningWriter++;
             WriterCanStart.release();
         }
    }

    void write() throws IOException {
        Thread t = Thread.currentThread();
        System.out.println(t.getName()+" try...");
        StartWrite();
        System.out.println(t.getName()+" writing...");
        try { Thread.sleep(100); } catch (InterruptedException e) {}


        ArrayList<Zvetok> baz = new ArrayList<>();
        if(sad.size() == 0){
            baz.add(new Zvetok("fnleblb","1"));
            baz.add(new Zvetok("wegknqe","1"));
            baz.add(new Zvetok("lmlnsgq","1"));
            baz.add(new Zvetok("dgqegnqe","1"));
        }
        else{
            baz = sad;
        }
      //  baz.add(new Zvetok("sfgjpeg","0"));
      //  baz.remove(0);
        FileWriter writer = new FileWriter("outputBazaFile.txt");
        for(Zvetok str: baz) {
            if(baz.get(baz.size()-1) == str) writer.write(str.name+"-"+str.stan);
            else writer.write(str.name+"-"+str.stan+","+" ");
        }
        writer.close();
        StopWrite();
    }

    void StartWrite()
    {
        if ((RunningWriter>0) | (RunningReader>0))
        {
            WaitingWriter++;
            try {WriterCanStart.acquire();} catch(InterruptedException e){}
        }
        else RunningWriter++;
    }

    void StopWrite()
    {
        RunningWriter--;
        if (WaitingPriroda>0){
            while (WaitingPriroda>0)
            {
                RunningPriroda++;
                WaitingPriroda--;
                PrirodaCanStart.release();
            }
        }
        else if (WaitingReader>0)
        {
            while (WaitingReader>0)
            {
                RunningReader++;
                WaitingReader--;
                ReaderCanStart.release();
            }
        }
        else if (WaitingWriter>0) {
            WaitingWriter--;
            RunningWriter++;
            WriterCanStart.release();
        }
    }

    void polit(int n) throws IOException{
        Thread t = Thread.currentThread();
        System.out.println(t.getName()+" try...");
        startPolit();
        System.out.println(t.getName()+" writing...");
        try { Thread.sleep(100); } catch (InterruptedException e) {}

     for(int i = 0; i<sad.size();i++){
         if(i == n && sad.get(i).stan.equals("0")){
             sad.get(i).stan = "1";
         }
     }
     StopPolit();

    }

    void startPolit(){
        if ((RunningWriter>0)  | (WaitingPolit>0))
        {
            WaitingPolit++;
            try {PolitCanStart.acquire();} catch(InterruptedException e){}
        }
        else RinningPolit++;
    }

    void StopPolit()
    {
        RinningPolit--;
        if ((RinningPolit==0) & (WaitingWriter>0))
        {
            WaitingWriter--;
            RunningWriter++;
            WriterCanStart.release();
        }
    }


    void priroda(int n) throws IOException{
        Thread t = Thread.currentThread();
        System.out.println(t.getName()+" try...");
        startPriroda();
        System.out.println(t.getName()+" writing...");

        try { Thread.sleep(100); } catch (InterruptedException e) {}
        for(int i = 0; i<sad.size()-1;i++){
            if(i == n && sad.get(i).stan.equals("1")){
                sad.get(i).stan = "0";
            }
        }
        StopPriroda();
    }

    void startPriroda(){
        if ((RunningWriter>0)  | (WaitingPriroda>0))
        {
            WaitingPriroda++;
            try {PrirodaCanStart.acquire();} catch(InterruptedException e){}
        }
        else RunningPriroda++;
    }

    void StopPriroda()
    {
        RunningPriroda--;
        if ((RunningPriroda==0) & (WaitingWriter>0))
        {
            WaitingWriter--;
            RunningWriter++;
            WriterCanStart.release();
        }
    }

}



class WriterToFile implements Runnable
{
    Q q;
    int i=0;
    WriterToFile(Q q,int num)
    {
        this.q = q;
        new Thread(this, "Writer "+num).start();
    }
    public void run()
    {
        while (q.work) {
            try {
                q.write();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;}
    }

}

class ReaderToConsole implements Runnable
{
    Q q;
    int i=0;
    ReaderToConsole(Q q, int num)
    {
        this.q = q;
        new Thread(this, "Reader "+num).start();
    }
    public void run()
    {
        while (q.work) {
            try {
                q.readD();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;}
    }
}

class PolitZcvetok implements Runnable{

    int i = 0;
    Q q;
    PolitZcvetok(Q q, int num){
        this.q = q;
        new Thread(this,"PolitZcvetok"+num).start();
    }
    public void run(){
        while(q.work){
            try{
                q.polit(1);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            i++;}
    }
}


class Priroda implements Runnable{
    Q q;
    int i = 0;
    Priroda(Q q, int num){
        this.q = q;
        new Thread(this,"Priroda"+num).start();
    }
    public void run(){
        while(q.work){
            try{
                q.priroda(1);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            i++;}
    }
}