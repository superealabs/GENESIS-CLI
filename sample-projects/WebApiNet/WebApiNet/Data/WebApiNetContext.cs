namespace WebApiNet.Data;

using Microsoft.EntityFrameworkCore;
using Models;

public class WebApiNetContext : DbContext
{
    public WebApiNetContext(DbContextOptions<WebApiNetContext> options) : base(options)
    {
    }
    public DbSet<House> Houses { get; set; }
    public DbSet<Owner> Owners { get; set; }
    
}
