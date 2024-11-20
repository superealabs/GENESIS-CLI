using System.Linq.Expressions;
using WebApiNet.Models;
using WebApiNet.Repositories.Interface;
using WebApiNet.Services.Interface;

namespace WebApiNet.Services.Implementation;

public class OwnerService : IOwnerService
{
	private readonly IOwnerRepository _repository;

	public OwnerService(IOwnerRepository repository)
	{
		_repository = repository;
	}

	public async Task<IEnumerable<Owner>> GetAllAsync(
	params Expression<Func<Owner, object?>>[]? includeProperties)
	{
		return await _repository.GetAllWithIncludes(includeProperties);
	}

	public async Task<Owner?> GetByIdAsync(int id,
	params Expression<Func<Owner, object?>>[]? includeProperties)
	{
		return await _repository.GetByIdAsync(id, includeProperties);
	}

	public async Task AddAsync(Owner owner)
	{
		await _repository.AddAsync(owner);
	}

	public async Task UpdateAsync(Owner owner)
	{
		await _repository.UpdateAsync(owner);
	}

	public async Task DeleteAsync(int id)
	{
		await _repository.DeleteAsync(id);
	}

	public async Task<bool> ExistsAsync(int id)
	{
		return await _repository.ExistsAsync(id);
	}
}
