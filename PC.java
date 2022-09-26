import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class PC
{
    public static void main(String args[]) {
        Q q = new Q();
        Writer w1 = new Writer(q,1);
        Writer w2 = new Writer(q,2);
        Writer w3 = new Writer(q,3);
        Writer w4 = new Writer(q,4);
        Reader r1 = new Reader(q,1);
        Reader r2 = new Reader(q,2);
        Reader r3 = new Reader(q,3);
        Reader r4 = new Reader(q,4);
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


class Baza{
    String fio;
    String tel;
    Baza(String f, String t){
        this.fio = f;
        this.tel = t;
    }

    void display(){
        System.out.print(fio);
        System.out.print("-");
        System.out.println(tel);
    }
}


class Q
{

    static ArrayList<Baza> baza = new ArrayList<>();
    int [] n = new int[10];
    boolean work = true;
    Semaphore ReaderCanStart=new Semaphore(0,true);
    Semaphore WriterCanStart=new Semaphore(0,true);
    int WaitingReader = 0; int RunningReader = 0;
    int WaitingWriter = 0; int RunningWriter = 0;
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
        ArrayList<Baza> baz = new  ArrayList<>();
        for(int i =0; i<vec.size()-1; i++){
            baz.add(new Baza(vec.get(i), vec.get(i + 1)));
            i+=1;
        }
        baza = baz;
        for(int i =0; i <baz.size(); i++ ){
            if(baza.get(i).fio.equals("wegknqe")){
                System.out.print("finding tel :");
                System.out.println(baza.get(i).tel);
                break;
            }
        }
        for(int i =0; i <baz.size(); i++ ){
            if(baza.get(i).tel.equals("62576456")){
                System.out.print("finding fio :");
                System.out.print(baza.get(i).fio);
            }
        }

        int m = calculate(n);
        System.out.println(t.getName()+": "+m);
        StopRead();
        return m;
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
        if ((RunningReader==0) & (WaitingWriter>0))
        {
            WaitingWriter--;
            RunningWriter++;
            WriterCanStart.release();
        }
    }



    void write(int [] n) throws IOException {
        Thread t = Thread.currentThread();
        System.out.println(t.getName()+" try...");
        StartWrite();
        System.out.println(t.getName()+" writing...");
        try { Thread.sleep(100); } catch (InterruptedException e) {}


        ArrayList<Baza> baz = new ArrayList<>();
        if(baza.size() == 0){
            baz.add(new Baza("fnleblb","1242155"));
            baz.add(new Baza("wegknqe","85102502"));
            baz.add(new Baza("lmlnsgq","62576456"));
            baz.add(new Baza("dgqegnqe","1725892"));
        }
        else{
             baz = baza;
        }
        baz.add(new Baza("sfgjpeg","285092"));
        baz.remove(0);
        FileWriter writer = new FileWriter("outputBazaFile.txt");
        for(Baza str: baz) {
            if(baz.get(baz.size()-1) == str) writer.write(str.fio+"-"+str.tel);
            else writer.write(str.fio+"-"+str.tel+","+" ");
        }
        writer.close();





        this.n = n;
        StopWrite();
        System.out.println(t.getName()+": "+toS(n));
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
        if (WaitingReader>0)
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

    String toS(int [] n)
    {
        String s="";
        for(int i=0; i<n.length; i++)
            s+=n[i]+" ";

        return s;
    }

    public int calculate(int [] m)
    {
        int min=Integer.MAX_VALUE;
        for(int i=0; i<m.length; i++){
            if(m[i]<min)
                min=m[i];
        }
        return min;
    }
}

class Writer implements Runnable
{
    Q q;
    int i=0;
    Writer(Q q,int num)
    {
        this.q = q;
        new Thread(this, "Writer "+num).start();
    }
    public void run()
    {
        while (q.work) {
            try {
                q.write(creatRandom(10));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;}
    }

    private int[] creatRandom(int n)
    {
        int [] m = new int[n];
        for(int i=0; i<n; i++)
            m[i] = (int)Math.round(Math.random()*100);
        return m;
    }
}

class Reader implements Runnable
{
    Q q;
    int i=0;
    Reader(Q q, int num)
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