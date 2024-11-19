namespace WebApiNet.Repositories.Generic;

using System.ComponentModel.DataAnnotations;
using System.Linq.Expressions;
using Microsoft.EntityFrameworkCore;
using Data;

public class Repository<T, TKey>: IRepository<T, TKey>, IDisposable, IAsyncDisposable where T : class
{

    private readonly DbSet<T> _dbSet;
    private readonly WebApiNetContext _context;

    public Repository(WebApiNetContext context)
    {
        _context = context;
        _dbSet = context.Set<T>();
    }

    public async ValueTask DisposeAsync()
    {
        await _context.DisposeAsync();
    }

    public void Dispose()
    {
        _context.Dispose();
    }

    public async Task<IEnumerable<T>> GetAllWithIncludes(params Expression<Func<T, object?>>[]? includeProperties)
    {
        var query = GetQuery(_dbSet, includeProperties);
        query = query.OrderBy(e => EF.Property<object>(e, GetKeyPropertyName()));
        return await query.ToListAsync();
    }     

    public async Task<T?> GetByIdAsync(TKey id, params Expression<Func<T, object?>>[]? includeProperties)
    {
        var keyPropertyName = GetKeyPropertyName();
        var query = GetQuery(_dbSet, includeProperties);
        return await query.FirstOrDefaultAsync(e => EF.Property<TKey>(e, keyPropertyName)!.Equals(id));
    }

    public async Task AddAsync(T entity)
    {
        await _dbSet.AddAsync(entity);
        await _context.SaveChangesAsync();
    }

    public async Task UpdateAsync(T entity)
    {
        _context.Entry(entity).State = EntityState.Modified;
        await _context.SaveChangesAsync();
    }

    public async Task DeleteAsync(TKey id)
    {
        var entity = await GetByIdAsync(id);
        if (entity == null) 
            throw new KeyNotFoundException($"Entity with ID {id} not found.");
        _dbSet.Remove(entity);
        await _context.SaveChangesAsync();
    }

    public async Task<bool> ExistsAsync(TKey id)
    {
        if (EqualityComparer<TKey>.Default.Equals(id, default)) return false;

        var keyPropertyName = GetKeyPropertyName();

        if (string.IsNullOrEmpty(keyPropertyName))
        throw new InvalidOperationException($"No key property found for type {typeof(T).Name}");

        return await _dbSet.AnyAsync(e =>
            EF.Property<TKey>(e, keyPropertyName) != null &&
            EF.Property<TKey>(e, keyPropertyName)!.Equals(id));
    }

    private IQueryable<T> GetQuery(IQueryable<T> query, params Expression<Func<T, object?>>[]? includeProperties)
    {
        if (includeProperties != null)
            foreach (var includeProperty in includeProperties)
                query = query.Include(includeProperty);

        return query;
    }

    private string GetKeyPropertyName()
    {
        var keyProperty = typeof(T).GetProperties()
            .FirstOrDefault(prop => Attribute.IsDefined(prop, typeof(KeyAttribute)));

        if (keyProperty == null)
            throw new InvalidOperationException($"No key property found for type {typeof(T).Name}");

        return keyProperty.Name;
    }
}
