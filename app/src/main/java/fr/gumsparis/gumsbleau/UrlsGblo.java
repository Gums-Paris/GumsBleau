package fr.gumsparis.gumsbleau;

public enum UrlsGblo {

    SORTIE("https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=prochsortie&format=json"),
    LISTE("https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=listesorties&format=json");

    private String url = "";

    UrlsGblo(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}
