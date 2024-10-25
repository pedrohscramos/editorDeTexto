module br.com.ordepsomar.editordetexto {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;


    opens br.com.ordepsomar.editordetexto to javafx.fxml;
    exports br.com.ordepsomar.editordetexto;
}