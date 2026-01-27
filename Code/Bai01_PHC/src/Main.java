import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Books> listBook = new ArrayList<>();
        Scanner x = new Scanner(System.in);

        String msg = """
            \n================ MENU QUẢN LÝ SÁCH ================
            1. Thêm 1 cuốn sách
            2. Xóa 1 cuốn sách theo mã (ID)
            3. Thay đổi thông tin sách theo mã
            4. Xuất toàn bộ danh sách sách
            5. Tìm sách có tựa đề chứa "Lập trình"
            6. Lọc tối đa K cuốn sách có giá <= P
            7. Tìm kiếm sách theo danh sách tác giả
            0. Thoát chương trình
            --------------------------------------------------
            Chọn chức năng: """;

        int chon = 0;
        do {
            System.out.print(msg);
            try {
                chon = Integer.parseInt(x.nextLine());

                switch (chon) {
                    case 1 -> {
                        Books b = new Books();
                        b.input();
                        listBook.add(b);
                    }
                    case 2 -> {
                        System.out.print("Mã cần xóa: ");
                        int bid = Integer.parseInt(x.nextLine());
                        Books f = listBook.stream()
                                .filter(p -> p.getId() == bid)
                                .findFirst()
                                .orElseThrow();
                        listBook.remove(f);
                        System.out.print("Đã xóa thành công!! Vui lòng kiểm tra lại ");
                    }
                    case 3 -> {
                        System.out.print("Mã cần sửa: ");
                        int bid = Integer.parseInt(x.nextLine());
                        Books f = listBook.stream()
                                .filter(p -> p.getId() == bid)
                                .findFirst()
                                .orElseThrow();
                        f.input();
                        System.out.print("Đã sửa thành công! Vui lòng kiểm tra lại ");
                    }
                    case 4 -> listBook.forEach(Books::output);
                    case 5 -> listBook.stream()
                            .filter(b -> b.getTitle().toLowerCase().contains("lập trình"))
                            .forEach(Books::output);
                    case 6 -> {
                        System.out.print("K: ");
                        int k = Integer.parseInt(x.nextLine());
                        System.out.print("P: ");
                        long p = Long.parseLong(x.nextLine());
                        listBook.stream()
                                .filter(b -> b.getPrice() <= p)
                                .limit(k)
                                .forEach(Books::output);
                    }
                    case 7 -> {
                        System.out.print("Tác giả : ");
                        String raw = x.nextLine();
                        Set<String> s = Arrays.stream(raw.split(","))
                                .map(String::trim)
                                .collect(Collectors.toSet());
                        listBook.stream()
                                .filter(b -> s.contains(b.getAuthor()))
                                .forEach(Books::output);
                    }
                }
            } catch (Exception e) {}
        } while (chon != 0);
    }
}