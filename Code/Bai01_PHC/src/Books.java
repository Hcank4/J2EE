import java.util.Scanner;

public class Books {
    private int id;
    private String title;
    private String author;
    private long price;

    public Books() {}

    public Books(int id, String title, String author, long price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public void input() {
        Scanner x = new Scanner(System.in);
        System.out.print("Nhập mã sách: ");
        this.id = Integer.parseInt(x.nextLine());
        System.out.print("Nhập tên sách: ");
        this.title = x.nextLine();
        System.out.print("Nhập tác giả : ");
        this.author = x.nextLine();
        System.out.print("Nhập giá tiền: ");
        this.price = Long.parseLong(x.nextLine());
    }

    public void output() {
        String msg = """
            BOOK: mã = %d, tên = %s, tác giả = %s, giá =%d""".formatted(id, title, author, price);
        System.out.println(msg);
    }
}