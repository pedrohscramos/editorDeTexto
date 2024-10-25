package br.com.ordepsomar.editordetexto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.*;
import java.util.Objects;

public class SimpleTextEditor extends Application {

    private TextArea textArea;
    private TreeView<File> fileTreeView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Editor de texto");

        textArea = new TextArea();
        textArea.setWrapText(true);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu formatMenu = new Menu("Format");

        MenuItem newFileItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");

        fileMenu.getItems().addAll(newFileItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);


        menuBar.getMenus().addAll(fileMenu);

        newFileItem.setOnAction(e -> newFile());
        openItem.setOnAction(e -> openFile(primaryStage));
        saveItem.setOnAction(e -> saveFile(primaryStage));
        exitItem.setOnAction(e -> primaryStage.close());



        File rootDirectory = new File(System.getProperty("user.home"));
        TreeItem<File> rootItem = createNode(rootDirectory);
        fileTreeView = new TreeView<>(rootItem);
        fileTreeView.setShowRoot(true);

        // Ação para abrir arquivos ao clicar duas vezes no arquivo
        fileTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue().isFile()) {
                    openSelectedFile(selectedItem.getValue());
                }
            }
        });

        // Layout do editor com árvore de arquivos à esquerda
        HBox contentPane = new HBox(fileTreeView, textArea);
        fileTreeView.setPrefWidth(250);

        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(contentPane);

        Scene scene = new Scene(layout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Cria recursivamente um nó de árvore para um diretório e seus arquivos/ subdiretórios
    private TreeItem<File> createNode(File file) {
        TreeItem<File> treeItem = new TreeItem<>(file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if(files != null) {
                for (File childFile : files) {
                    treeItem.getChildren().add(createNode(childFile));
                }
            }
        }
        return treeItem;
    }

    private void newFile(){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("New File");
        confirmAlert.setHeaderText("Alterações não salvas serão perdidas");
        confirmAlert.setContentText("Deseja salvar arquivo atual antes de criar um novo?");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType discardButton = new ButtonType("Discard");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmAlert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);
        ButtonType result = confirmAlert.showAndWait().orElse(cancelButton);

        if(result == saveButton){
            Stage stage = (Stage) textArea.getScene().getWindow();
            saveFile(stage);
        } else if (result == discardButton){
            textArea.clear();
        }
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.clear();
                String line;
                while((line = reader.readLine()) != null){
                    textArea.appendText(line + "\n");
                }
            } catch (IOException e) {
                showError("Erro ao abrir o arquivo: " + e.getMessage());
            }
        }
    }

    private void openSelectedFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            textArea.setText(content.toString());
        } catch (IOException e) {
            showError("Error opening file: " + e.getMessage());
        }
    }

    private void saveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                writer.write(textArea.getText());
            } catch (IOException e) {
                showError("Erro ao salvar o arquivo: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }



    public static void main(String[] args) {
        launch(args);
    }
}