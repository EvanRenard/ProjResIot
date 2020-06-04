package TestServeur;

import Chenillard.Chenillard;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.InetSocketAddress;

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

@Path("/entry-point")
public class EntryPoint {

    static InetSocketAddress local;
    static InetSocketAddress remote ;

    static KNXNetworkLinkIP knx ;

    static ProcessCommunicator pc;
    static Chenillard t ;
    static int  vitesse=300 ;
    //definit la direction de depart du chenillard
    static String  direction="gd" ;
    //sert a savoir si le chenillard est en marche
     static Boolean  etat_start=false ;
     //tous les push présent servent a ne pas rentrer deux fois dans la condition lors de l'appuie sur un bouton
    static   boolean push_etat = false;
    //sert a declarer le nombre de lampe du chenillard , c'est ici qu'il faut changer pour augmenter le nombre de lampe
    static   int nblampes = 4;
    static   boolean push_vitesse = false;
    static   boolean push_vitesse2 = false;
    static   boolean push_direction = false;
    @GET
    @Path("/home")
    @Produces(MediaType.TEXT_HTML)
    public File desk() throws KNXException, InterruptedException {
        local = new InetSocketAddress("192.168.1.105", 7000);
        remote = new InetSocketAddress("192.168.1.202", 3671);

        knx = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, new TPSettings());
        pc = new ProcessCommunicatorImpl(knx);
        t = new Chenillard(pc,nblampes,direction,vitesse);

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
                String addresse = ((CEMILData) arg0.getFrame()).getDestination().toString();
                System.out.println("addresse : "+addresse);

                //si on appuie sur le bouton 1
                if (addresse.equals("1/0/1") ) {
                    if (push_etat) {
                        push_etat = false;
                    } else {
                        push_etat = true;
                    }
                    //on rentre dans la boucle que une fois et non pas lors du push and pull du bouton
                    if (push_etat) {
                        //si le chenillard n'est pas en route
                        if(!etat_start){
                            etat_start=true;
                            t = new Chenillard(pc, nblampes, direction, vitesse);
                            //on le lance
                            t.start();
                        } else {
                            etat_start=false;
                            //sinon on le stop
                            t.stopRunning();
                        }

                    }

                }
                if (addresse.equals("1/0/2")) {

                        if (push_vitesse) {
                            push_vitesse = false;
                        } else {
                            push_vitesse = true;
                        }
                        if (push_vitesse) {
                            if(etat_start) {
                                //si on appuie sur le bouton 2 on stop le chenillard
                            t.stopRunning();
                            //on augmente la vitesse en baissant le temps d'arret entre deux lampes
                            vitesse = vitesse - 50;
                            if (vitesse < 0) {
                                vitesse = 30;
                            }
                            //puis on lance le nouven
                            t = new Chenillard(pc, nblampes, direction, vitesse);
                            t.start();
                        }else{
                                vitesse = vitesse - 50;
                                if (vitesse < 0) {
                                    vitesse = 10;
                                }
                            }
                    }
                }
                if (addresse.equals("1/0/3")) {
                    if(push_vitesse2){
                        push_vitesse2=false;
                    }else{
                        push_vitesse2 =true;
                    }
                    if(push_vitesse2) {
                        if (etat_start) {
                            t.stopRunning();
                            vitesse = vitesse + 50;
                            t = new Chenillard(pc, nblampes, direction, vitesse + 50);
                            t.start();
                        }else {
                            vitesse = vitesse + 50;
                        }
                    }
                }
                if (addresse.equals("1/0/4")) {
                    if(push_direction){
                        push_direction =false;
                    }else{
                        push_direction =true;
                    }
                    if (push_direction) {
                        if(etat_start) {
                            t.stopRunning();
                            if (direction == "dg")
                                direction = "gd";
                            else
                                direction = "dg";
                            t = new Chenillard(pc, nblampes, direction, vitesse);
                            t.start();
                        }else {
                            if (direction == "dg")
                                direction = "gd";
                            else
                                direction = "dg";
                        }
                    }
                }
            }


            public void confirmation(FrameEvent arg0) {
                System.out.println("Link Listener confirmed");
            }
        });






        return new File("./src/main/Home_page.html");
    }

    @GET
    @Path("/start_stop")
    @Produces(MediaType.TEXT_HTML)
    public File run() {
        if(!etat_start){
            t = new Chenillard(pc, nblampes, direction, vitesse);
            t.start();
            etat_start=true;
        } else {
            etat_start=false;
            t.stopRunning();
        }

        return new File("./src/main/Home_page.html");
    }


    @GET
    @Path("/augmenter")
    @Produces(MediaType.TEXT_HTML)
    public File augmenter() {
        if(etat_start) {
            t.stopRunning();
            vitesse = vitesse - 50;
            if (vitesse < 0) {
                vitesse = 30;
            }
            t = new Chenillard(pc, nblampes, direction, vitesse);
            t.start();
        } else{
            vitesse = vitesse - 50;
            if (vitesse < 0) {
                vitesse = 10;
            }
        }
        return new File("./src/main/Home_page.html");

    }

    @GET
    @Path("/diminuer")
    @Produces(MediaType.TEXT_HTML)
    public File diminuer() {
        if(etat_start) {
            t.stopRunning();
            vitesse = vitesse + 50;
            t = new Chenillard(pc, nblampes, direction, vitesse + 50);
            t.start();
        } else {
            vitesse = vitesse + 50;
        }
        return new File("./src/main/Home_page.html");
    }

    @GET
    @Path("/inverser")
    @Produces(MediaType.TEXT_HTML)
    public File inverser() {
        if(etat_start){
        t.stopRunning();
        if (direction == "dg")
            direction = "gd";
        else
            direction = "dg";
        t = new Chenillard(pc, nblampes, direction, vitesse);
        t.start();}
        else{
            if (direction == "dg")
                direction = "gd";
            else
                direction = "dg";
        }
        return new File("./src/main/Home_page.html");
    }


}
