package ristogo.common.net.entities;

public class PageFilter extends Entity
{
	private static final long serialVersionUID = -5593528692510897595L;

	private int page;
	private int perPage;

	public PageFilter(int page, int perPage)
	{
		if (page < 0)
			throw new IllegalArgumentException("Page must be non-negative.");
		this.page = page;
		if (perPage <= 0)
			throw new IllegalArgumentException("PerPage must be positive.");
		this.perPage = perPage;
	}

	public PageFilter(int page)
	{
		this(page, 10);
	}

	public int getPage()
	{
		return page;
	}

	public int getPerPage()
	{
		return perPage;
	}

	public void setPage(int page)
	{
		if (page < 0)
			throw new IllegalArgumentException("Page must be non-negative.");
		this.page = page;
	}

	public void setPerPage(int perPage)
	{
		if (perPage <= 0)
			throw new IllegalArgumentException("PerPage must be positive.");
		this.perPage = perPage;
	}
}
