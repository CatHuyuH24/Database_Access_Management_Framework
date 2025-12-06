# ĐẶC TẢ ĐỒ ÁN

# DATABASE ACCESS MANAGEMENT (DAM) FRAMEWORK

## GIỚI THIỆU

- Dữ liệu thường được lưu trữ trong các cơ sở dữ liệu quan hệ như MS SQL, MySQL, PostgreSQL, Oracle… Dữ liệu này được thể hiện thành các bảng, các dòng, các cột.
- Trong khi đó, phương pháp lập trình hướng đối tượng xem dữ liệu là các đối tượng.
- Do đó, ngoài các framework như ADO.NET, JDBC…, người ta xây dựng trên đó có framework hỗ trợ ánh xạ giữa dữ liệu quan hệ thành dữ liệu đối tượng, giúp cho việc lập trình tương tác CSDL trở nên dễ dàng hơn.
- Nhiều framework đã ra đời như NHibernate, LINQ, Entity Framework trên môi trường .NET hoặc Hibernate trên Java.
- Trong đồ án này, các nhóm sinh viên được yêu cầu tìm hiểu cấu trúc của các Database Access Management (DAM) Framework, từ đó xây dựng cho mình một framework tương tự.

## CÁC YÊU CẦU CHUNG

- Thực hiện theo nhóm từ 2-4 người.
- Phải vận dụng ít nhất 4 trong số 23 mẫu thiết GoF để xây dựng Framework.

## CÁC YÊU CẦU CHỨC NĂNG CƠ BẢN TRONG FRAMEWORK

- **DAM Framework của bạn cần hỗ trợ các thao tác cơ bản:**
  - Kết nối CSDL
  - Thao tác insert
  - Thao tác update
  - Thao tác delete
  - Thao tác select dữ liệu có where, group by, having
  - Đóng kết nối CSDL
- **ORM:** Xây dựng các lớp cơ sở để truy xuất các bảng dữ liệu dưới dạng đối tượng.
  - Mỗi lớp có thể tương ứng với một bảng (trong hầu hết trường hợp trừ trường hợp quan hệ N-N).
  - Có thể sử dụng reflection hoặc attribute (C#) hoặc annotation (Java) để đọc các thông tin mô tả các trường dữ liệu, tên bảng, quan hệ với các lớp khác.
- Xây dựng các lớp kết nối cơ bản hỗ trợ truy vấn CSDL.
- Xây dựng các lớp xử lý chuyển các thao trên lớp đối tượng thành SQL.
- Các lớp truy xuất dữ liệu (bảng) có các getter, setter, save, delete tương ứng với dòng dữ liệu.

## CÁC YÊU CẦU CHỨC NĂNG MỞ RỘNG TRONG FRAMEWORK

- Thể hiện quan hệ giữa các bảng bằng quan hệ của các đối tượng.

## CÁC LƯU Ý KHÁC

- Tuyệt đối không vi phạm các vấn đề về bản quyền, đạo mã nguồn, ý tưởng.
- Các framework sinh viên có thể tham khảo như: Dapper, PetaPoco,...
- **Một vài link tham khảo:**
  - DomainModel: http://www.martinfowler.com/eaaCatalog/domainModel.html
  - ActiveRecord: http://www.martinfowler.com/eaaCatalog/activeRecord.html
  - Và các mẫu khác trong phần TableDataGateway, LazyLoad, Data Mapper…: http://www.martinfowler.com/eaaCatalog/index.html
- Framework phải được xây dựng sao cho đảm bảo tính kế thừa và mở rộng để thêm hệ quản trị CSDL dễ dàng.
  - Ví dụ, khi xây dựng, kèm với framework, bạn cài đặt các thao tác cụ thể với CSDL MS SQL.
  - Tuy nhiên sau đó, dựa trên framework này, bạn có thể xây dựng các thao tác với CSDL MySQL, SQLite, PostgresSQL hoặc Oracle một cách dễ dàng.
- Do đồ án yêu cầu xây dựng framework, nên nhóm sinh viên chỉ được phép gọi các hàm trong thư viện ADO.NET hoặc JDBC để thao tác CSDL.
- **Không** được sử dụng LINQ, Entity Framework, NHibernate hay Hibernate.

## CÁC THÔNG TIN CHUNG

- **Deadline nộp bài:** sau ngày thi lý thuyết (dự kiến).
- **Lưu ý:** **Không** nhận các bài nộp trễ với bất kỳ lý do nào. Sinh viên cần chủ động nộp bài sớm.
- **Điểm:** 2,5 điểm
  - Báo cáo: 1,0 điểm
  - Chương trình và các nội dung khác: 1,5 điểm

## CÁC QUY ĐỊNH CHUNG

**Bài nộp bao gồm:**

1.  **Báo cáo gồm hai phần:**

    - **Sơ đồ lớp của toàn bộ framework bao gồm:**
      - Sơ đồ lớp
      - Giải thích ý nghĩa của từng lớp
    - **Nêu rõ các mẫu (ít nhất 4 mẫu) sử dụng trong framework bao gồm:**
      - Tên mẫu
      - Sơ đồ lớp ứng với từng mẫu
      - Đoạn code sử dụng mẫu
      - Ý nghĩa khi sử dụng mẫu
    - **Bảng danh sách các tính năng hoàn thành bao gồm:**
      - Tên tính năng
      - Mức độ hoàn thành

2.  **Mã nguồn**

3.  **Nếu có sử dụng thư viện hỗ trợ hay có lưu ý gì khác** (ví dụ kết nối cơ sở dữ liệu, cài đặt các phần mềm khác hỗ trợ) cần phải có các tài liệu dưới dạng file .doc hoặc .pdf

4.  **Hướng dẫn sử dụng**

5.  **Hướng dẫn cài đặt**

6.  **Các tài liệu khác (nếu có)**

7.  **Film demo**

8.  **Tập tin cài đặt (setup.exe)**

...

**Cấu trúc tổ chức thư mục như sau tương ứng với (Lưu ý tên của thư mục không gõ dấu):**

```text
<Framework name>
  ├── <1.Documents>
  ├── <2.Source code>
  ├── <3.Functions List>
  └── <4.Others>
```

_Thư mục "Framework name" ở trên được nén thành một file nén duy nhất MSSV1-MSSV2-MSSV3-…-MSSVn.zip_
