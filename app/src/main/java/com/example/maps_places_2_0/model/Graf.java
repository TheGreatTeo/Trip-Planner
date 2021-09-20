package com.example.maps_places_2_0.model;

import java.util.LinkedList;

public class Graf {

    public static class Muchie{
        int sursa;
        int destinatie;
        long distanta;

        public long getDistanta() {
            return distanta;
        }

        public int getDestinatie(){ return destinatie;}

        public Muchie(int sursa, int destinatie, long distanta){
            this.sursa = sursa;
            this.destinatie = destinatie;
            this.distanta = distanta;
        }
    }

    public static class Drum{
        int nr_destinatii;
        LinkedList<Muchie>[] listaAdiacenta;

        public LinkedList<Muchie>[] getListaAdiacenta() {
            return listaAdiacenta;
        }

        public Drum(int nr_destinatii){
            this.nr_destinatii = nr_destinatii;
            listaAdiacenta = new LinkedList[nr_destinatii];

            for(int i = 0; i < nr_destinatii; i++){
                listaAdiacenta[i] = new LinkedList<>();
            }
        }

        public int getNr_destinatii() {
            return nr_destinatii;
        }

        public void adaugaMuchie(int sursa, int destinatie, long distanta){
            Muchie muchie = new Muchie(sursa,destinatie,distanta);
            listaAdiacenta[sursa].addFirst(muchie);
        }

        public void printGraf(){
            for (int i = 0; i <nr_destinatii ; i++) {
                LinkedList<Muchie> list = listaAdiacenta[i];
                for (int j = 0; j <list.size() ; j++) {
                    System.out.println("vertex-" + i + " is connected to " +
                            list.get(j).destinatie + " with weight " +  list.get(j).distanta);
                }
            }
        }

    }

}
