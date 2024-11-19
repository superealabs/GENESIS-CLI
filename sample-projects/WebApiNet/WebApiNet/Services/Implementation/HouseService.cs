using System.Linq.Expressions;
using WebApiNet.Models;
using WebApiNet.Repositories.Interface;
using WebApiNet.Services.Interface;

namespace WebApiNet.Services.Implementation;

public class HouseService : IHouseService
{
	private readonly IHouseRepository _repository;

	public HouseService(IHouseRepository repository)
	{
		_repository = repository;
	}

	public async Task<IEnumerable<House>> GetAllAsync(
	params Expression<Func<House, object?>>[]? includeProperties)
	{
		return await _repository.GetAllWithIncludes(includeProperties);
	}

	public async Task<House?> GetByIdAsync(long id,
	params Expression<Func<House, object?>>[]? includeProperties)
	{
		return await _repository.GetByIdAsync(id, includeProperties);
	}

	public async Task AddAsync(House house)
	{
		await _repository.AddAsync(house);
	}

	public async Task UpdateAsync(House house)
	{
		await _repository.UpdateAsync(house);
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
