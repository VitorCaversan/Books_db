# Books_db
A Database of books with blobs as book covers
# Books_db: João Vitor Caversan e Alfons Heiermann
Com base em arquivos postados na página da disciplina (livros-db-MySQL.sql e livos-db.csv), foi requisitado criar um banco de dados e preencher a tabela livros e inserir as capas destes nos campos mediumblob.

Os requisitos eram que a importação do .csv fosse feita em comando de linha e a inserção das capas fosse feita via um programa Java com JDBC.

## Relatório dos passos e comandos utilizados no carregamento

Nesta seção serão abordados os métodos utilizados para a criação e carregamento de todos os dados no banco de dados.

### Criação do banco de dados e suas tabelas

Primeiramente, para que todo o processo pudesse ser feito apenas fazendo o carregamento do arquivo sql em terminal, foram adicionadas 3 linhas de código no seu início, que tratavam da criação do banco de dados em si e sua utilização como base para a criação das tabelas:

```c
   DROP DATABASE IF EXISTS livros_db;
   CREATE DATABASE IF NOT EXISTS livros_db;
   USE livros_db;
```

Para executar o script foi utilizado o seguinte comando:

```terminal
   sudo mysql --local-infile=1 -u root -p < livros-db-MySQL.sql
```
### Carregamento do arquivo .csv

Com o banco de dados pronto para utilização, a próxima etapa era a de carregamento dos dados na tabela _livros_ a partir do arquivo .csv. 

Como o arquivo não continha dados para todos os campos da tabela, os dados faltantes foram preenchidos à mão, sendo estes referentes aos seguintes campos:

> genero_id, descricao, preco, estoque e reserva

Para que fosse possível carregar um arquivo externo no MySQL, uma flag global do programa teve de ser modificada para _true_ da seguinte forma:

```terminal
   mysql -u root -p
      set global local_infile=1;
      quit;
```

Com isso feito, os comandos de linha utilizados para carregar os dados na tabela foram:

```terminal
   mysql --local-infile=1 -u root -p
      load data local infile 'livros-db.csv' into table livros fields terminated by ';' enclosed by '"' lines terminated by '\n' ignore 1 rows (livro_id, titulo, autor, edicao, ano, editora, genero_id,
      descricao, preco, estoque, reserva);
```

### Carregando as capas dos livros no campo _BLOB_

Para o carregamento das capas dos livros, fornecidas em formato .jpg, nos campos mediumBlob, foi feito um programa em Java com JDBC.

Para a leitura do arquivo, se utilizou um buffer na forma de um ByteArrayStrem. Para o carregamento do buffer se utilizou de um Prepared Statement com duas variáveis, sendo a primeira o blob da capa e a segunda o id do livro em questão. Sendo assim, o código final ficou:

```c
   package Browsing;
   import java.sql.*;
   import java.io.*;

   public class BookBrowser {
      
      public static void main(String args[]) throws Exception // Not handling any exception here
      {
         // Load MySQL driver
         // Can be fully removed when using MySQL. Must only be used when
         // multiple connections are beeing set.
         Class.forName("com.mysql.cj.jdbc.Driver");

         // Start server connection
         String url      = "jdbc:mysql://localhost/livros_db";
         String user     = "root";
         String password = "password";
         Connection con = DriverManager.getConnection(url, user, password);

         // Create a statement: an execution line
         Statement start = con.createStatement();
         PreparedStatement prepStm = con.prepareStatement("update livros set capa = ? where livro_id = ?");  

         ResultSet res_set = start.executeQuery("select livro_id from livros");

         while (res_set.next())
         {
               System.out.println(res_set.getInt(1));

               //Loading BLOB
               ByteArrayOutputStream buffer = new ByteArrayOutputStream();
               FileInputStream file;
               file = new FileInputStream("/home/joao/Documents/FACULDADE/6_PERIODO/BancoDeDados/trabalhos/Livros_db/livros/"+res_set.getInt(1)+".jpg");
               int readByte = file.read();
               while(readByte != -1){
                  buffer.write(readByte);
                  readByte = file.read();
               }
               
               prepStm.setInt(2, res_set.getInt(1));
               prepStm.setBytes(1, buffer.toByteArray());
               prepStm.executeUpdate();          
         }                     
      }   
   }
```

Neste ponto se termina a parte de carregamento da atividade solicitada. Isso deixa as tabelas *pedidos, pedidos_detalhe* e *usuarios* sem preenchimento, porém, para esse trabalho, nenhuma destas foi utilizada.