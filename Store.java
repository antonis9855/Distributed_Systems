import  java.util.LinkedList;
public class Store {
    String name;
    int latitude;
    int longitude;
    String category;
    int stars;
    int votes;
    String logo;
    LinkedList<Product> products;

    public Store(String name, int latitude, int longitude, String category, int stars, int votes, String logo) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.stars = stars;
        this.votes = votes;
        this.logo = logo;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void addProduct(Product product){
        products.add(product);
    }

    public void to_json(String file_name){

    }

}
