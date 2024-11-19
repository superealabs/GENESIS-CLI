namespace WebApiNet.Services.Generic;

using System.Linq.Expressions;

public interface IService<T, in TKey> where T : class
{
    Task<IEnumerable<T>> GetAllAsync(params Expression<Func<T, object?>>[]? includeProperties);
    Task<T?> GetByIdAsync(long id, params Expression<Func<T, object?>>[]? includeProperties);
    Task AddAsync(T entity);
    Task UpdateAsync(T entity);
    Task DeleteAsync(TKey id);
    Task<bool> ExistsAsync(TKey id);
}
