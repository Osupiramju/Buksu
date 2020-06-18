package buksu.app;

import androidx.appcompat.app.AppCompatActivity;

public class Book extends AppCompatActivity {

    private String author, bookTitle, genre;
    private Integer user_id, numberPages;

    public Book() { }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getBookTitle() { return bookTitle; }

    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public Integer getNumberPages() { return numberPages; }

    public void setNumberPages(Integer numberPages) { this.numberPages = numberPages; }

    public Integer getUser_id() { return user_id; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }
}
