package com.example.service;

/*
    Toy with runnable
 */
public class RunARound implements Runnable{

    String name=null;
    long cnt = 0;
    long milliSecs = 1000;

    public RunARound(String nme, long secs){
        name = nme;
        // Minimum of 1 second
        milliSecs = Long.max(1,secs) * 1000;
    }


    public void stopIt(){
        cnt += 10000000;
    }


    @Override
    public void run() {

        while (cnt <10000000){
            cnt++;
            System.out.println(name + " : " + cnt);
            try {
                Thread.sleep(milliSecs);
            } catch (Exception e ) {
                e.printStackTrace();
            }
        }


    }


}
