package br.com.generateQueryToJavaClass;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GenerateQueryToJava {

	/**
	 * @param args
	 * @throws SQLException 
	 */

	
	
	public static void main(String[] args) throws SQLException {
		PreparedStatement prst = null;
		ResultSet rs = null;
		Connection conn = null;
		Connection connection = null;  
		try {  
		    // Load the JDBC driver  
		    connection = loadJDBCDriver();  
		    
			// Configurar a Locale da conexão Jdbc
			conn = connection;
			prst = conn.prepareStatement("ALTER SESSION SET NLS_COMP = BINARY");
			prst.executeUpdate();
			
			StringBuffer sql = obterQuery();
						
			prst = conn.prepareStatement(sql.toString());
							
			rs = prst.executeQuery();
			
			ResultSetMetaData meta = rs.getMetaData();  
		    String nomeClasse = obterNomeClasse();
		   
			StringBuffer textoClasse = escreverClasse(meta, nomeClasse);
			
			connection.close();		    
		    JTextArea textAreaSaida = new JTextArea(textoClasse.toString());
			JScrollPane scrollPaneSaida = new JScrollPane(textAreaSaida);  
			textAreaSaida.setLineWrap(true);  
			textAreaSaida.setWrapStyleWord(true); 
			scrollPaneSaida.setPreferredSize( new Dimension( 500, 500 ) );
			JOptionPane.showMessageDialog(null, scrollPaneSaida, "Saida - Classe ", JOptionPane.YES_NO_OPTION);
			
			int opcao = JOptionPane.showConfirmDialog(null, "Deseja salvar a classe?", "Sim ou não?", JOptionPane.YES_NO_OPTION);  
			if(opcao == 0){
			   salvarClasse(nomeClasse,textoClasse.toString());
		   }
			
		    
		} catch (ClassNotFoundException e) {  
			JOptionPane.showMessageDialog(null, "Não foi possivel encontrar o driver de banco: " + e.getMessage()); 
			 
		} catch (SQLException e) {  
			JOptionPane.showMessageDialog(null, e.getMessage());  
		}  
		
	}

	private static StringBuffer escreverClasse(ResultSetMetaData meta,
			String nomeClasse) throws SQLException {
			StringBuffer textoClasse = new StringBuffer("");
			textoClasse.append("\n");
			textoClasse.append("public class "+nomeClasse+" {");
			textoClasse.append("\n");
			for(int i=1;i<=meta.getColumnCount();i++){  
				 String tipo = null;
				int type = meta.getColumnType(i);
				
				tipo = verificaTipoDeDados(type);
				textoClasse.append("\t private"+ tipo);
				
				String columnName = meta.getColumnName(i);
				textoClasse.append(columnName.toLowerCase()+"; "); 
					textoClasse.append("\n");
			
			}
			
			for(int i=1;i<=meta.getColumnCount();i++){  
				String columnName =	meta.getColumnName(i).substring(0, 1).toUpperCase() + meta.getColumnName(i).substring(1).toLowerCase();
				textoClasse.append("\n");
				int type = meta.getColumnType(i);
				String tipo = verificaTipoDeDados(type);
				
				textoClasse.append("\t public void set"+columnName+"(" +tipo+columnName.toLowerCase()+") { \n");
				textoClasse.append("\t \t this."+columnName.toLowerCase()+" = "+columnName.toLowerCase()+"; \n");
				textoClasse.append("\t } ");
				textoClasse.append("\n");
				textoClasse.append("\t public " + tipo + " get"+columnName+"(){ \n");
				textoClasse.append("\t \t return "+columnName.toLowerCase()+"; \n");
				textoClasse.append("\t }");
			
			
			}
			
			textoClasse.append("\n");
			textoClasse.append("}");
			return textoClasse;
	}

	private static String verificaTipoDeDados(int type) {
		String tipo;
		if (type == Types.VARCHAR || type==Types.CHAR) {
			tipo =" String ";
		}else{
			
		}
		
		switch (type) {
		case Types.VARCHAR:
			tipo =" String ";
			break;
		case  Types.CHAR:
			tipo =" String ";
			break;
		case  Types.INTEGER:
			tipo =" Integer ";
			break;
		case  Types.DECIMAL:
			tipo =" BigDecimal ";
			break;
			
		case  Types.DOUBLE:
			tipo =" Double ";
			break;
			
		case  Types.NUMERIC:
			tipo =" BigDecimal ";
			break;	
			
		default:
			tipo =" Indefinido ";
			break;
		}
		
		
		
		return tipo;
	}

	private static String obterNomeClasse() {
		String nomeClasse = JOptionPane.showInputDialog("Qual o nome da classe?", "Digite aqui.");

		if (nomeClasse==null || nomeClasse.equals("Digite aqui.")) {
			JOptionPane.showMessageDialog(null, "Programa encerrado. \n Você não digitou o nome da classe. ");
			System.exit(0);
		}
		return nomeClasse;
	}

	private static StringBuffer obterQuery() {
		JTextArea textArea = new JTextArea("Coloque sua query aqui!");
		JScrollPane scrollPane = new JScrollPane(textArea);  
		textArea.setLineWrap(true);  
		textArea.setWrapStyleWord(true); 
		scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
		JOptionPane.showMessageDialog(null, scrollPane, "Entrada - Query ", JOptionPane.YES_NO_OPTION);
		StringBuffer sql = new StringBuffer("");
		if (textArea.getText()==null || textArea.getText().equals("Coloque sua query aqui!") ) {
			JOptionPane.showMessageDialog(null, "Programa encerrado. \n Você não informou a query. ");
			System.exit(0);
		}else{				
			 sql.append(textArea.getText());
		}
		return sql;
	}

	private static Connection loadJDBCDriver() throws ClassNotFoundException,
			SQLException {
		Connection connection;
		String driverName = "oracle.jdbc.driver.OracleDriver";  
		Class.forName(driverName);  
  
		Conexao conexao = obterConexao();
		
		/*Conexao conexao = new Conexao();
		conexao.setUrl("jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST =localhost )(PORT = 1521))(CONNECT_DATA = (sid = teste)(service_name = localhost) ) )");  
		conexao.setUsername("usr_seu_usuario");  
		conexao.setPassword("senha");*/
		
		connection = DriverManager.getConnection(conexao.getUrl(), conexao.getUsername().trim(), conexao.getPassword().trim());
		return connection;
	}

	private static Conexao obterConexao() {
		Conexao conexao = new Conexao();
		
		try {  
			
			File arquivo;  
			// Lendo do arquivo  
	        arquivo = new File("config.txt");  
	        FileInputStream fis = new FileInputStream(arquivo);  
	
	        StringBuilder stringLine = new StringBuilder();
	        int ln;  
	        int cont =0;
	        while ( (ln = fis.read()) != -1 ) {  
	        	stringLine.append( (char)ln );
	        	
	        	if (((char)ln)=='\n') {
	        		
	        		
	        		switch (cont) {
					case 0:
						conexao.setUrl(stringLine.toString());
						break;

					case 1:
						conexao.setUsername(stringLine.toString());
						break;
					
		        	case 2:
						conexao.setPassword(stringLine.toString());
						break;
					}
	        		
	        		stringLine  = new StringBuilder();
	        		cont++;
				}
	        }  
	
	        fis.close(); 
	        
	    
	        
	   }  
	    catch (Exception ee) {  
	        ee.printStackTrace();  
	    }
		return conexao;  
	}

	
	/*public Conexao obterConfiguracao() {
		 try {  
	            // Gravando no arquivo  
	            File arquivo;  
	  
	            arquivo = new File("arquivo.txt");  
	            FileOutputStream fos = new FileOutputStream(arquivo);  
	            String texto = "quero gravar este texto no arquivo";  
	            fos.write(texto.getBytes());  
	            texto = "\nquero gravar este texto AQUI TAMBEM";  
	            fos.write(texto.getBytes());  
	            fos.close();  
	  
	            // Lendo do arquivo  
	            arquivo = new File("config.txt");  
	            FileInputStream fis = new FileInputStream(arquivo);  
	  
	            int ln;  
	            while ( (ln = fis.read()) != -1 ) {  
	                System.out.print( (char)ln );  
	            }  
	  
	            fis.close();  
	        }  
	        catch (Exception ee) {  
	            ee.printStackTrace();  
	        }  
		
	}*/
	
	public static void salvarClasse(String nomeClasse, String conteudo) {
		try {  
	        // Gravando no arquivo  
	      
	        String dir = JOptionPane.showInputDialog("Digite o diretorio onde deseja salvar a classe (exemplo C:\\)","C:\\");
	        
	        File arquivo = new File(dir);
	        if(!arquivo.exists()){
				  
	        	int opcao = JOptionPane.showConfirmDialog(null,  
				            "Deseja criar o Diretorio? ("+dir+")", "Sim ou não?", JOptionPane.YES_NO_OPTION);  
					if(opcao == 0){
						new File(dir).mkdir();  
				   }else{
					   JOptionPane.showMessageDialog(null, "Programa encerrado. \n Você não informou o diretório correto. ");
						System.exit(0);
				   }

			  }
	        
	        
	        
	        if(!dir.substring(dir.length()-1, dir.length()).equals("\\")){
	        	dir = dir+"\\";
	        }
	        arquivo = new File(dir+nomeClasse+".java");  
	        FileOutputStream fos = new FileOutputStream(arquivo);  
	        fos.write(conteudo.getBytes()); 	         
	        fos.close(); 
	    	JOptionPane.showMessageDialog(null,"Arquivo gravado na pasta: "+arquivo.toString());
	       
	    }  
	    catch (Exception ee) {  
	    	JOptionPane.showMessageDialog(null,ee.getMessage());  
	    }  
	}
	
	

}
