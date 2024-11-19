namespace WebApiNet.Repositories.Generic;

using System.Linq.Expressions;

public interface IRepository<T, in TKey> where T : class
{
    Task<IEnumerable<T>> GetAllWithIncludes(params Expression<Func<T, object?>>[]? includeProperties);
    Task<T?> GetByIdAsync(TKey id, params Expression<Func<T, object?>>[]? includeProperties);
    Task AddAsync(T entity);
    Task UpdateAsync(T entity);
    Task DeleteAsync(TKey id);
    Task<bool> ExistsAsync(TKey id);
}
