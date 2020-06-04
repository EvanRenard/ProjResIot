package Chenillard;

import java.net.InetSocketAddress;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;

import tuwien.auto.calimero.KNXException;

import tuwien.auto.calimero.cemi.CEMILData;

import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class Main {
    static InetSocketAddress local;
    static InetSocketAddress remote ;

    static KNXNetworkLinkIP knx ;

    static ProcessCommunicator pc;
    static Chenillard  t ;
    static int  vitesse=300 ;
    static String  direction="gd" ;
    static   boolean push_etat = false;
    static   boolean etat = true;
    static   boolean push_vitesse = false;
    static   boolean push_vitesse2 = false;
    static   boolean push_direction = false;
    public static void main(String[] args) throws KNXException, InterruptedException, KNXException {
        // TODO Auto-generated method stub






        local = new InetSocketAddress("192.168.1.105", 7000);
        remote = new InetSocketAddress("192.168.1.202", 3671);

        knx = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, new TPSettings());

        pc = new ProcessCommunicatorImpl(knx);


        // chenillard qui possede 4 lampes , commence a la 0 de gauche a droite
        //pc.write(new GroupAddress("0/0/1"), !pc.readBool(new GroupAddress("0/0/1")));

        knx.addLinkListener(new NetworkLinkListener() {

            int i =0;
            public void linkClosed(CloseEvent arg0) {
                System.out.println("link closed");
                t.cancel();
                pc.close();
                knx.close();
            }


            public void indication(FrameEvent arg0) {
                System.out.println("srcadress " + arg0.getSource());
                //System.out.println("targetadress:" + ((CEMILData) arg0.getFrame()).getDestination());
                String addresse = ((CEMILData) arg0.getFrame()).getDestination().toString();

                System.out.println("addresse : "+addresse);


                if (addresse.equals("1/0/1") ) {
                    if (push_etat) {
                        push_etat = false;
                    } else {
                        push_etat = true;
                    }
                    if (push_etat) {
                        if(etat){
                        System.out.println("lancement thread");
                        etat=false;
                        t = new Chenillard(pc, 4, direction, vitesse);

                        t.start();
                    } else {
                        etat=true;
                        t.stopRunning();
                    }

                }

                }
                if (addresse.equals("1/0/2")) {
                    if(push_vitesse){
                        push_vitesse =false;
                    }else{
                        push_vitesse =true;
                    }
                    if(push_vitesse) {
                        t.stopRunning();
                        vitesse=vitesse-50;
                        if (vitesse<0){
                            vitesse=10;
                        }
                        t = new Chenillard(pc, 4, direction, vitesse);
                        t.start();
                    }
                }
                if (addresse.equals("1/0/3")) {
                    if(push_vitesse2){
                        push_vitesse2=false;
                    }else{
                        push_vitesse2 =true;
                    }
                    if(push_vitesse2) {
                        t.stopRunning();
                        vitesse=vitesse+50;
                        t = new Chenillard(pc, 4, direction, vitesse+50);
                        t.start();
                    }
                }
                if (addresse.equals("1/0/4")) {
                    if(push_direction){
                        push_direction =false;
                    }else{
                        push_direction =true;
                    }
                    if (push_direction) {
                            t.stopRunning();
                            if (direction == "dg")
                                direction = "gd";
                            else
                                direction = "dg";
                            t = new Chenillard(pc, 4, direction, vitesse);
                            t.start();

                    }
                }
            }


            public void confirmation(FrameEvent arg0) {
                System.out.println("Link Listener confirmed");
            }
        });

        Thread.sleep(1000000);

    }


}
