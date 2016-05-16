package recordlinkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import no.priv.garshol.duke.Comparator;
import no.priv.garshol.duke.comparators.LongestCommonSubstring;
import no.priv.garshol.duke.comparators.SoundexComparator;

/**
 * 
 * 
 * <code>RecordLinkageImpl</code> é usada para comparação dois arquivos,
 * utilizando a técnica Record Linkage que tenham seu conteudo no formato JSON
 * Lines e obter registros dos arquivos em questão que tenham informações sobre
 * uma mesma entidade.
 * 
 * @author Gleydson Rocha
 * @version 1.0
 *
 */
public class RecordLinkageImpl {

	private static final String NO_BLOCK = "NOB";

	private boolean blocagemAtiva;

	private boolean quebrarChaveBlocagem;

	private ArquivoComparacao arquivoComparacao1;

	private ArquivoComparacao arquivoComparacao2;

	private Map<String, List<JSONObject>> valoresArquivo1;

	private Map<String, List<JSONObject>> valoresArquivo2;

	private List<Link<JSONObject>> resultados;

	private double corteLimite = 94.0;

	Set<String> produtos = new HashSet<String>();
	Set<String> precos = new HashSet<String>();

	/**
	 * Cria um <code>RecordLinkageImpl</code> para efetuar a comparação dos
	 * arquivos passaados por parâmetro.
	 * <p>
	 * Não será executado a etapa de blocagem.
	 * <p>
	 *
	 * @param ac1
	 *            Arquivo para comparação.
	 * @param ac2
	 *            Arquivo para comparação.
	 * @exception IllegalArgumentException
	 *                Ou se a quantidade de campos em ambos arquivos não forem a
	 *                mesma.
	 */
	public RecordLinkageImpl(ArquivoComparacao ac1, ArquivoComparacao ac2) {
		this(ac1, ac2, false, false);
	}

	/**
	 * Cria um <code>RecordLinkageImpl</code> para efetuar a comparação dos
	 * arquivos passaados por parâmetro.
	 * <p>
	 * Caso a <code>blocagemAtiva</code> for true o conteúdo do arquivo será
	 * divido em blocos para melhor desempenho.
	 * <p>
	 *
	 * @param ac1
	 *            Arquivo para comparação.
	 * @param ac2
	 *            Arquivo para comparação.
	 * @param blocagemAtiva
	 *            Variável que determina a execução da blocagem.
	 * @exception IllegalArgumentException
	 *                Se a <code>blocagemAtiva</code>for true e os aquirvos
	 *                passados não tiverem o campo definodo a ser usado para
	 *                blocagem. Ou se a quantidade de campos em ambos arquivos
	 *                não forem a mesma.
	 */
	public RecordLinkageImpl(ArquivoComparacao ac1, ArquivoComparacao ac2, boolean blocagemAtiva) {
		this(ac1, ac2, blocagemAtiva, false);
	}

	/**
	 * Cria um <code>RecordLinkageImpl</code> para efetuar a comparação dos
	 * arquivos passaados por parâmetro.
	 * <p>
	 * Caso a <code>blocagemAtiva</code> seja <b>true</b> o conteúdo do arquivo
	 * será divido em blocos para melhor desempenho.
	 * <p>
	 * <p>
	 * Caso a <code>quebrarChaveBlocagem</code> seja <b>true</b> será usado
	 * apenas os três primeiros caracteres da chave de blocagem gerada para
	 * dividir o conteúdo dos arquivos será quebrada para uma melhor precisão.
	 * <p>
	 *
	 * @param ac1
	 *            Arquivo para comparação.
	 * @param ac2
	 *            Arquivo para comparação.
	 * @param blocagemAtiva
	 *            Variável que determina a execução da blocagem.
	 * @param quebrarChaveBlocagem
	 *            Determina se será executada uma quebra na chave dos blocos..
	 * @exception IllegalArgumentException
	 *                Se a <code>blocagemAtiva</code>.for <b>true</b> e os
	 *                aquirvos passados não tiverem o campo definodo a ser usado
	 *                para blocagem. Ou se a quantidade de campos em ambos
	 *                arquivos não forem a mesma.
	 */
	public RecordLinkageImpl(ArquivoComparacao ac1, ArquivoComparacao ac2, boolean blocagemAtiva,
			boolean quebrarChaveBlocagem) {

		if (blocagemAtiva && ac1.getCampoBlocagem() == null)
			throw new IllegalArgumentException("Não foi definido campo para a blocagem no parâmetro ac1.");

		if (blocagemAtiva && ac2.getCampoBlocagem() == null)
			throw new IllegalArgumentException("Não foi definido campo para a blocagem no parâmetro ac2.");

		if (ac1.getCampos().size() != ac2.getCampos().size())
			throw new IllegalArgumentException(
					"Quantidade de campos do parâmetro ac1 não combina com a quantidade de campos do parâmetro ac2.");

		this.arquivoComparacao1 = ac1;
		this.arquivoComparacao2 = ac2;
		this.blocagemAtiva = blocagemAtiva;
		this.quebrarChaveBlocagem = quebrarChaveBlocagem;
		this.resultados = null;

		// Melhor limite de corte para esse caso, comparação de produto e preços
		this.corteLimite = 94.0;
	}

	/**
	 * 
	 * Executa a comparação dos arquivos informados.
	 * 
	 * @throws JSONException
	 *             Se houver erro na sintaxe do JSON ou a chave de blocagem ou
	 *             algum campo informado for inválido.
	 * @throws IOException
	 *             Erro ao ler conteúdo
	 */
	public void executar() throws JSONException, IOException {

		long l1 = System.currentTimeMillis();

		valoresArquivo1 = lerDados(arquivoComparacao1);
		valoresArquivo2 = lerDados(arquivoComparacao2);

		comparar();
		System.out.println("Quan Resultados: " + resultados.size());

		long l2 = System.currentTimeMillis();
		System.out.println("Tempo: " + ((double) (l2 - l1) / 1000.0) + " segundos");
	}

	/**
	 * Ler dados do arquivo passado no parâmetro.
	 */
	private Map<String, List<JSONObject>> lerDados(ArquivoComparacao arquivo) throws JSONException, IOException {
		BufferedReader bufferReader = new BufferedReader(
				new InputStreamReader(arquivo.getConteudo(), Charset.forName("UTF-8")));

		if (blocagemAtiva)
			return lerDadosComBlocagem(bufferReader, arquivo.getCampoBlocagem());
		else
			return lerDadosSemBlocagem(bufferReader);

	}

	/**
	 * Ler dados sem dividir em blocos.
	 */
	private Map<String, List<JSONObject>> lerDadosSemBlocagem(BufferedReader bufferReader)
			throws JSONException, IOException {

		Map<String, List<JSONObject>> dados = new HashMap<String, List<JSONObject>>();

		String linha;
		List<JSONObject> aux = new ArrayList<JSONObject>();
		while ((linha = bufferReader.readLine()) != null) {
			aux.add(new JSONObject(linha));
		}

		dados.put(NO_BLOCK, aux);

		return dados;

	}

	/**
	 * Ler dados dividindo em blocos.
	 */
	private Map<String, List<JSONObject>> lerDadosComBlocagem(BufferedReader bufferReader, String campoBlocagem)
			throws JSONException, IOException {

		Map<String, List<JSONObject>> dados = new HashMap<String, List<JSONObject>>();

		String linha = null;
		JSONObject jObject = null;
		String chaveBloco = null;
		List<JSONObject> aux = null;
		while ((linha = bufferReader.readLine()) != null) {
			jObject = new JSONObject(linha);
			chaveBloco = obterChaveBloco(jObject.getString(campoBlocagem));
			aux = dados.get(chaveBloco);
			if (aux == null) {
				aux = new ArrayList<JSONObject>();
				aux.add(jObject);
				dados.put(chaveBloco, aux);
			} else {
				aux.add(jObject);
			}
		}

		return dados;
	}

	/**
	 * Obtém chave do bloco apartir do campo pré-configurado.
	 */
	private String obterChaveBloco(String campoParaBlocagem) {
		String chaveBloco = SoundexComparator.soundex(campoParaBlocagem.toUpperCase());
		if (quebrarChaveBlocagem && chaveBloco.length() >= 3)
			chaveBloco = chaveBloco.substring(0, 3);

		return chaveBloco;
	}

	/**
	 * Compara o(s) bloco(s) de <code>valoresArquivo1</code> com o(s) bloco(s)
	 * conrrespondente(s) em <code>valoresArquivo2</code>.
	 */
	private void comparar() {

		resultados = new ArrayList<Link<JSONObject>>();

		Set<String> chavesArquivo1 = valoresArquivo1.keySet();

		List<JSONObject> blocoArquivo1;
		List<JSONObject> blocoArquivo2;
		for (String chave : chavesArquivo1) {
			blocoArquivo2 = valoresArquivo2.get(chave);
			if (blocoArquivo2 != null) {
				blocoArquivo1 = valoresArquivo1.get(chave);
				compararListasComRemocao(blocoArquivo1, blocoArquivo2);
			}

		}

	}

	/**
	 * Compara a lista de registros de dois blocos correspondentes.
	 */
	@SuppressWarnings("unused")
	private void compararListas(List<JSONObject> blocoArquivo1, List<JSONObject> blocoArquivo2) {
		for (JSONObject object1 : blocoArquivo1) {
			for (JSONObject object2 : blocoArquivo2) {
				compararJSONObject(object1, object2);
			}
		}
	}

	/**
	 * Compara a lista de registros de dois blocos correspondentes.
	 * <p>
	 * Caso o índice de semelhança seja maior que o corte superior o segundo
	 * item e removido da lista
	 */
	private void compararListasComRemocao(List<JSONObject> blocoArquivo1, List<JSONObject> blocoArquivo2) {
		for (int i = 0; i < blocoArquivo1.size(); i++) {
			for (int j = 0; j < blocoArquivo2.size();) {
				if (compararJSONObject(blocoArquivo1.get(i), blocoArquivo2.get(j)))
					blocoArquivo2.remove(j);
				else
					j++;
			}
		}
	}

	/**
	 * Aplica algoritmos da técnica de Record Linkage para verificar a
	 * semelhança entre dois registros
	 * 
	 * Valores do cálculo que estiverem acima do corteLimite seram adicionados
	 * na lista de resultados
	 * 
	 * @return true se o indice de semelhança estiver acima do
	 *         <code>corteLimite</code>.
	 */
	private boolean compararJSONObject(JSONObject object1, JSONObject object2) {

		double peso = 0.0;
		String campo1;
		String campo2;
		Comparator c = new LongestCommonSubstring();
		for (int i = 0; i < arquivoComparacao1.getCampos().size(); i++) {
			campo1 = object1.getString(arquivoComparacao1.getCampos().get(i));
			campo2 = object2.getString(arquivoComparacao2.getCampos().get(i));

			// Considera os 30 primeiros caracteres de cada campo para uma
			// melhor performace.
			if (campo1.length() > 30)
				campo1 = campo1.substring(0, 31);
			if (campo2.length() > 30)
				campo2 = campo2.substring(0, 31);

			// Cálculo para que o valor fique entre 0 e 100
			peso += (double) c.compare(campo1.toLowerCase(), campo2.toLowerCase());
		}

		peso = (peso / arquivoComparacao1.getCampos().size()) * 100;

		if (peso >= corteLimite) {
			resultados.add(new Link<JSONObject>(object1, object2, peso));
			return true;
		}

		return false;

	}

	/**
	 * 
	 * @return Resultados obtidos após o processamento.
	 */
	public List<Link<JSONObject>> obterResultados() {
		return resultados;
	}

	public void setBlocagemAtiva(boolean blocagemAtiva) {
		this.blocagemAtiva = blocagemAtiva;
	}

	public boolean isBlocagemAtiva() {
		return blocagemAtiva;
	}

	public void setQuebrarChaveBlocagem(boolean quebrarChaveBlocagem) {
		this.quebrarChaveBlocagem = quebrarChaveBlocagem;
	}

	public boolean isQuebrarChaveBlocagem() {
		return quebrarChaveBlocagem;
	}

	public ArquivoComparacao getArquivoComparacao1() {
		return arquivoComparacao1;
	}

	public ArquivoComparacao getArquivoComparacao2() {
		return arquivoComparacao2;
	}

	public double getCorteLimite() {
		return corteLimite;
	}

	public void setCorteLimite(double corteLimite) {
		this.corteLimite = corteLimite;
	}

}
