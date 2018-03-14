package Refactoring01_Singleton_Iterator;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TelaOperacoes {
	private Stage mainStage; 
	private Scene cenaEntrada;
	private Scene cenaOperacoes;

	private TextField tfValorOperacao;
	private TextField tfSaldo;

	public TelaOperacoes(Stage mainStage, Scene telaEntrada) {																					// conta
		this.mainStage = mainStage;
		this.cenaEntrada = telaEntrada;
	}

	public Scene getTelaOperacoes() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        String dadosCorr = Contas.getInstance().getContaEmUso().getNumero()+" : "+
        		           Contas.getInstance().getContaEmUso().getCorrentista();
        Text scenetitle = new Text(dadosCorr);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        
        Label tit = new Label("Ultimos movimentos");
        grid.add(tit,0,1);

        // Seleciona apenas o extrato da conta atual (camada de negócios)
        ListView<Operacao> extrato = new ListView<>(Operacoes.getInstance().setContaEmUso());
        extrato.setPrefHeight(140);
        grid.add(extrato, 0, 2);

        tfSaldo = new TextField();
        tfSaldo.setDisable(true);
        tfSaldo.setText(""+Contas.getInstance().getContaEmUso().getSaldo());
        HBox valSaldo = new HBox(20);        
        valSaldo.setAlignment(Pos.BOTTOM_LEFT);
        valSaldo.getChildren().add(new Label("Saldo"));
        valSaldo.getChildren().add(tfSaldo);
        grid.add(valSaldo, 0, 3);        

        tfValorOperacao = new TextField();
        HBox valOper = new HBox(30);        
        valOper.setAlignment(Pos.BOTTOM_CENTER);
        valOper.getChildren().add(new Label("Valor operação"));
        valOper.getChildren().add(tfValorOperacao);
        grid.add(valOper, 1, 1);        

        Button btnCredito = new Button("Crédito");
        Button btnDebito = new Button("Débito");
        Button btnVoltar = new Button("Voltar");
        HBox hbBtn = new HBox(20);
        hbBtn.setAlignment(Pos.TOP_CENTER);
        hbBtn.getChildren().add(btnCredito);
        hbBtn.getChildren().add(btnDebito);
        hbBtn.getChildren().add(btnVoltar);
        grid.add(hbBtn, 1, 2);
        
        
        
        btnCredito.setOnAction(e->{
        	try {
        	  double valor = Integer.parseInt(tfValorOperacao.getText());
        	  // ---------- Camada de negócios -------------------
        	  if (valor < 0.0) {
        		  throw new NumberFormatException("Valor invalido");
        	  }
        	  Contas.getInstance().getContaEmUso().deposito(valor);
        	  GregorianCalendar date = new GregorianCalendar();
        	  Operacao op = new Operacao(
        			  date.get(GregorianCalendar.DAY_OF_MONTH),
        			  date.get(GregorianCalendar.MONTH+1),
        			  date.get(GregorianCalendar.YEAR),
        			  date.get(GregorianCalendar.HOUR),
        			  date.get(GregorianCalendar.MINUTE),
        			  date.get(GregorianCalendar.SECOND),
        			  Contas.getInstance().getContaEmUso().getNumero(),
        			  Contas.getInstance().getContaEmUso().getStatus(),
        			  valor,
        			  0);
        	  // Esta adicionando em duas listas (resolver na camada de negocio)
              Operacoes.getInstance().insereNovaOperacao(op);       	  
        	  tfSaldo.setText(""+Contas.getInstance().getContaEmUso().getSaldo());
        	}catch(NumberFormatException ex) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Valor inválido !!");
				alert.setHeaderText(null);
				alert.setContentText("Valor inválido para operacao de crédito!!");

				alert.showAndWait();
        	}        	
        });
        
        btnDebito.setOnAction(e->{
        	try {
          	  double valor = Integer.parseInt(tfValorOperacao.getText());
          	  if (valor < 0.0 || valor > Contas.getInstance().getContaEmUso().getSaldo()) {
          		  throw new NumberFormatException("Saldo insuficiente");
          	  }
          	  Contas.getInstance().getContaEmUso().retirada(valor);
        	  GregorianCalendar date = new GregorianCalendar();
        	  Operacao op = new Operacao(
        			  date.get(GregorianCalendar.DAY_OF_MONTH),
        			  date.get(GregorianCalendar.MONTH+1),
        			  date.get(GregorianCalendar.YEAR),
        			  date.get(GregorianCalendar.HOUR),
        			  date.get(GregorianCalendar.MINUTE),
        			  date.get(GregorianCalendar.SECOND),
        			  Contas.getInstance().getContaEmUso().getNumero(),
        			  Contas.getInstance().getContaEmUso().getStatus(),
        			  valor,
        			  1);
        	  // Esta adicionando em duas listas (resolver na camada de negocio)
              Operacoes.getInstance().insereNovaOperacao(op);        	  
        	  tfSaldo.setText(""+Contas.getInstance().getContaEmUso().getSaldo());
          	}catch(NumberFormatException ex) {
  				Alert alert = new Alert(AlertType.WARNING);
  				alert.setTitle("Valor inválido !!");
  				alert.setHeaderText(null);
  				alert.setContentText("Valor inválido para operacao de débito!");

  				alert.showAndWait();
          	}        	
        });

        btnVoltar.setOnAction(e->{
        	mainStage.setScene(cenaEntrada);
        });
		
        cenaOperacoes = new Scene(grid);
        return cenaOperacoes;
	}

}
