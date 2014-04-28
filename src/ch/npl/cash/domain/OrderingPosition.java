package ch.npl.cash.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class OrderingPosition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "orderingRef")
	private Ordering ordering;
	
	@ManyToOne
	@JoinColumn(name = "articleRef")
	private Article article;
	
	public Ordering getOrdering() {
		return ordering;
	}
	
	public void setOrdering(Ordering ordering) {
		this.ordering = ordering;
	}
	
	public Article getArticle() {
		return article;
	}
	
	public void setArticle(Article article) {
		this.article = article;
	}

	public int getArticleId() {
		return article.getId();
	}

	public String getArticleName() {
		return article.getName();
	}

	public double getArticlePrice() {
		return article.getPrice();
	}
}
