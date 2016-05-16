package recordlinkage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 
 * 
 * <code>ArquivoComparacao</code> obtém os dados a serem usados para comparação
 * com a técnica Record Linkgade,.
 *
 * @see recordlinkage.RecordLinkageImpl
 * 
 * @author Gleydson Rocha
 * @version 1.0
 *
 */
public class ArquivoComparacao {
	private InputStream conteudo;
	private String campoBlocagem;
	private List<String> campos;

	/**
	 * Cria um <code>ArvquivoComparacao</code> que contém o conteúdo a ser
	 * comparado.
	 * <p>
	 * Deve ser levado em consisideração a quantidade e ordem de itens na lista
	 * de campos, pois esssas informações serão levadas em consideração quando
	 * for feita a compaaração.
	 * <p>
	 *
	 * @param arquivo
	 *            Arquivo que contém o conteúdo a ser comparado.
	 * @param campos
	 *            Lista de campos a serem comparados.
	 * @throws FileNotFoundException
	 *             caso o arquivo não seja encontrado
	 * @throws IllegalArgumentException
	 *             Caso o conteúdo seja nulo ou se a lista de campos for nula ou
	 *             vazia.
	 * 
	 */
	public ArquivoComparacao(File arquivo, List<String> campos) throws FileNotFoundException {
		this(new FileInputStream(arquivo), campos, null);
	}

	/**
	 * Cria um <code>ArvquivoComparacao</code> que contém o conteúdo a ser
	 * comparado.
	 * <p>
	 * Deve ser levado em consisideração a quantidade e ordem de itens na lista
	 * de campos, pois esssas informações serão levadas em consideração quando
	 * for feita a compaaração.
	 * <p>
	 *
	 * @param conteudo
	 *            Conteúdo a ser comparado.
	 * @param campos
	 *            Lista de campos a serem comparados.
	 * @throws IllegalArgumentException
	 *             Caso o conteúdo seja nulo ou se a lista de campos for nula ou
	 *             vazia.
	 * 
	 */
	public ArquivoComparacao(InputStream conteudo, List<String> campos) {
		this(conteudo, campos, null);
	}

	/**
	 * Cria um <code>ArvquivoComparacao</code> que contém o conteúdo a ser
	 * comparado.
	 * <p>
	 * Deve ser levado em consisideração a quantidade e ordem de itens na lista
	 * de campos, pois esssas informações serão levadas em consideração quando
	 * for feita a compaaração.
	 * <p>
	 *
	 * @param conteudo
	 *            Conteúdo a ser comparado.
	 * @param campos
	 *            Lista de campos a serem comparados.
	 * @param campoBlocagem
	 *            Campo a ser usado para a blocagem.
	 * @throws IllegalArgumentException
	 *             Caso o conteúdo seja nulo ou se a lista de campos for nula ou
	 *             vazia.
	 * 
	 */
	public ArquivoComparacao(InputStream conteudo, List<String> campos, String campoBlocagem) {

		if (conteudo == null)
			throw new IllegalArgumentException("Parâmetro conteúdo não pode ser nulo.");

		if (campos == null || campos.isEmpty())
			throw new IllegalArgumentException("É necessário informar os campos a serem comparados.");

		this.conteudo = conteudo;
		this.campos = campos;
		this.campoBlocagem = campoBlocagem;
	}

	public List<String> getCampos() {
		return campos;
	}

	public String getCampoBlocagem() {
		return campoBlocagem;
	}

	public InputStream getConteudo() {
		return conteudo;
	}

}