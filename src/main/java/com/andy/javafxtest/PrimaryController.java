package com.andy.javafxtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javafx.fxml.FXML;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javax.xml.parsers.ParserConfigurationException;

public class PrimaryController {

    @FXML
    private CheckBox ckb;

    @FXML
    private void switchToThird() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    private TextArea txt;

    @FXML
    private void setTxt(TextArea txt) {

        PrintStream printStream = new PrintStream(new customOutputStream(txt));
        // System.setOut(printStream);
        System.setErr(printStream);

    }

    @FXML
    private TreeView<String> tree;

    @FXML
    private void getDir() throws Exception {

        //private int i = 1;
        //System.out.println(this.ckb.isSelected());
        // Node pdfico = new ImageView("img/pdf-icon-copy-min1.png");
        // tree.setExpanded(true);
        var homeDir = System.getProperty("user.home") + System.getProperty("file.separator");
        /*  TreeItem<String> home = new TreeItem<String>(homeDir.toString());
        home.setExpanded(true);
        tree.setRoot(home);
        home.setGraphic(new ImageView("img/folder_modernist_add.png"));
         */
        DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setInitialDirectory(new File(homeDir));

        var ff = dirchooser.showDialog(null);

        if (ff != null) {

            //   Runnable runnable = () -> {
            try (Stream<Path> paths = Files.walk(Paths.get(ff.getAbsolutePath()))) {

                List<String> pathList = paths
                        .parallel()
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".pdf"))
                        //.peek(System.out::println)
                        .map(p -> {
                            if (Files.isDirectory(p)) {

                                return p.toString() + "/";
                            }
                            return p.toString();
                        })
                        .collect(Collectors.toList());

                //TreeItem.forEach(TreeItem-> TreeItem.setGraphic(new ImageView("")));
                //  PrintStream standardOut = System.out;
                Runnable runnable = () -> {
                    try {
                        com.andy.helpers.PageCount.printToXml(pathList, ckb.isSelected());

                    } catch (IOException ex) {
                        Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                Thread thread = new Thread(runnable);

                // Thread thread = new Thread(runnable);
                thread.start();
                /*           };
                    Thread thread = new Thread(runnable);
                    thread.start();*/
                TreeItem<String> home = new TreeItem<String>(ff.getCanonicalFile().toString());
                home.setExpanded(true);
                tree.setRoot(home);
                home.setGraphic(new ImageView("img/folder_modernist_add.png"));
                pathList.forEach(String -> home.getChildren().add(new TreeItem<String>(String, (new ImageView("img/document_a4.png")))));

            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }


            /*  for (String f:pathList){
                    System.out.printf("%s"+")"+"%s" +"%d"+"\n",i++, f, PageCount.efficientPDFPageCount(f));
                  // System.out.println(PageCount.efficientPDFPageCount(f));
                }*///paths.parallel().forEach(p-> System.out.println("Thread : " + Thread.currentThread().getName() + ", value: " + p));
        }//);

    }

}
