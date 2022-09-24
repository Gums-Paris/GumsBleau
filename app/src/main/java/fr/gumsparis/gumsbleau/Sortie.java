package fr.gumsparis.gumsbleau;

public class Sortie {

    String dateSortie;
    String lieuSortie;
    String numArticle;

    Sortie() {}

    void setDateSortie(String date) {this.dateSortie = date;}
    void setLieuSortie(String lieu) {this.lieuSortie = lieu;}
    void setNumArticle(String num) {this.numArticle = num;}

    String getDateSortie() {return dateSortie;}
    String getLieuSortie() {return lieuSortie;}
    String getNumArticle() {return numArticle;}
}
