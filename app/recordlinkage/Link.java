package recordlinkage;
/**
 *
 *
 * <code>Link</code> representa registros similares encontrados após uso
 * da técnica Record Linkgade.
 *
 * @see recordlinkage.RecordLinkageImpl
 * @see recordlinkage.ArquivoComparacao
 *
 *
 */
public class Link<T> {

	private T registroArquivo1;

	private T registroArquivo2;

	private double similaridade;

	public Link() {
	}

	/**
	 * Cria um <code>Link</code> que liga dois registros.
	 *
	 * @param registroArquivo1
	 *            Registro do aquivo1;.
	 * @param registroArquivo2
	 *            Registro do aquivo2;
	 */
	public Link(T registroArquivo1, T registroArquivo2, double similaridade) {
		this.registroArquivo1 = registroArquivo1;
		this.registroArquivo2 = registroArquivo2;
		this.similaridade = similaridade;
	}

	public T getRegistroArquivo1() {
		return registroArquivo1;
	}

	public void setRegistroArquivo1(T registroArquivo1) {
		this.registroArquivo1 = registroArquivo1;
	}

	public T getRegistroArquivo2() {
		return registroArquivo2;
	}

	public void setRegistroArquivo2(T registroArquivo2) {
		this.registroArquivo2 = registroArquivo2;
	}

	public double getSimilaridade() {
		return similaridade;
	}

	public void setSimilaridade(double similaridade) {
		this.similaridade = similaridade;
	}

}
