package Chenillard;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.process.ProcessCommunicator;

public class Chenillard extends Thread {

    ProcessCommunicator pc;
    boolean etat;

    int nblampes;
    //afin de gerer la boucle for de l'allumage des lampes en fonction de la direction du chenillard
    int sauvegarde_lampe;
    String direction;
    int vitesse;


    public Chenillard(ProcessCommunicator pc, int nblampes,
                      String direction,int vitesse) {
        super();
        this.pc = pc;
        etat = true;

        this.nblampes = nblampes;

        this.direction = direction;
        this.vitesse = vitesse;

    }



    @Override
    public void run() {

        etat=true;
        //tant que on veut qu'il marche
        while (etat) {
            try {

                //en fonction de la direction on lui change le for
                if(direction=="gd") {

                    sauvegarde_lampe=1;

                    for (int i=sauvegarde_lampe;i<nblampes+1;i++) {
                        //on retest si jamais on a pas decider d'areter le chenillard en plein milieu de la boucle for
                        if(etat) {
                            //on allume la lampe puis la réteind avec un temps decider dans le programme principal
                            pc.write(new GroupAddress("0/0/"+i), true);
                            Chenillard.sleep(vitesse);
                            pc.write(new GroupAddress("0/0/"+i), false);
                        }


                    }

                }

                if(direction=="dg") {

                    sauvegarde_lampe=nblampes;

                    for (int i=sauvegarde_lampe;i>nblampes-nblampes;i--) {
                        if(etat) {
                            pc.write(new GroupAddress("0/0/"+i), true);
                            Chenillard.sleep(vitesse);
                            pc.write(new GroupAddress("0/0/"+i), false);

                        }


                    }

                }


            } catch (KNXLinkClosedException |InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KNXTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KNXFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

    public void stopRunning() {
        //on met l'etat du thread a false pour que le chenillard s'arrete
        etat = false;
        Thread.currentThread().interrupt();
        Chenillard.currentThread().interrupt();

    }

    public void cancel() {

        // interruption du thread courant, c'est-à-dire le nôtre
        Chenillard.currentThread().interrupt();
        pc.close();
    }






}
