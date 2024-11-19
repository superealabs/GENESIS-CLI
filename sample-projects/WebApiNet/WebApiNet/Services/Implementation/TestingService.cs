using System.Linq.Expressions;
using WebApiNet.Models;
using WebApiNet.Repositories.Interface;
using WebApiNet.Services.Interface;

namespace WebApiNet.Services.Implementation;

public class TestingService : ITestingService
{
	private readonly ITestingRepository _repository;

	public TestingService(ITestingRepository repository)
	{
		_repository = repository;
	}

	public async Task<IEnumerable<Testing>> GetAllAsync(
	params Expression<Func<Testing, object?>>[]? includeProperties)
	{
		return await _repository.GetAllWithIncludes(includeProperties);
	}

	public async Task<Testing?> GetByIdAsync(long id,
	params Expression<Func<Testing, object?>>[]? includeProperties)
	{
		return await _repository.GetByIdAsync(id, includeProperties);
	}

	public async Task AddAsync(Testing testing)
	{
		await _repository.AddAsync(testing);
	}

	public async Task UpdateAsync(Testing testing)
	{
		await _repository.UpdateAsync(testing);
	}

	public async Task DeleteAsync(long id)
	{
		await _repository.DeleteAsync(id);
	}

	public async Task<bool> ExistsAsync(long id)
	{
		return await _repository.ExistsAsync(id);
	}
}
