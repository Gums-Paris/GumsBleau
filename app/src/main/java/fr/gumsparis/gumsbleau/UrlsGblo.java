package fr.gumsparis.gumsbleau;

public enum UrlsGblo {

    SORTIE("https://www.gumsparis.asso.fr/index.php?option=com_gblo&view=prochsortie&format=json"),
    LISTE("https://www.gumsparis.asso.fr/index.php?option=com_gblo&view=listesorties&format=json"),
    LISTEFUTURES("https://www.gumsparis.asso.fr/index.php?option=com_gblo&view=sortiesfutures&format=json");
//    SORTIE("http://10.0.2.2:8081/index.php?option=com_gblo&view=prochsortie&format=json"),
//    LISTE("http://10.0.2.2:8081/index.php?option=com_gblo&view=listesorties&format=json"),
//    LISTEFUTURES("http://10.0.2.2:8081/index.php?option=com_gblo&view=sortiesfutures&format=json");
//    SORTIE("https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=prochsortie&format=json"),
//    LISTE("https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=listesorties&format=json"),
//    LISTEFUTURES("https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=sortiesfutures&format=json");
//    API_LOCAL("http://10.0.2.2:8081/index.php");

    final private String url ;

    UrlsGblo(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}
