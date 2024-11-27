
using Steeltoe.Management.Endpoint.Info;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using Steeltoe.Discovery.Client;
using Steeltoe.Management.Endpoint.Health;
using Steeltoe.Management.Endpoint;
using Microsoft.OpenApi.Models;
using Microsoft.EntityFrameworkCore;
using WebApiNet.Data;
using WebApiNet.Repositories.Implementation;
using WebApiNet.Repositories.Interface;
using WebApiNet.Services.Implementation;
using WebApiNet.Services.Interface;

var builder = WebApplication.CreateBuilder(args);

var pathBase = builder.Configuration.GetValue<string>("PathBase");

builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "WebApiNet",               
        Version = "v1",                       
        Description = "Web Api NET"
    });
    if (!string.IsNullOrEmpty(pathBase))
    {
      c.AddServer(new OpenApiServer { Url = pathBase });
    }
});


// Add Steeltoe Actuators for application monitoring and diagnostics
builder.Services.AddAllActuators(builder.Configuration);
builder.Services.AddHealthActuator();
builder.Services.AddInfoActuator();

// Retrieve the local IP address dynamically
string localIpAddress = NetworkInterface
      .GetAllNetworkInterfaces()
      .SelectMany(nic => nic.GetIPProperties().UnicastAddresses)
      .Where(addr => addr.Address.AddressFamily == AddressFamily.InterNetwork && !IPAddress.IsLoopback(addr.Address))
      .Select(addr => addr.Address.ToString())
      .FirstOrDefault() ?? "127.0.0.1";

// Configure Kestrel to listen on both the machine's IP address and localhost
builder.WebHost.ConfigureKestrel(options =>
{
    options.Listen(IPAddress.Parse(localIpAddress), 9080);
    options.Listen(IPAddress.Loopback, 9080);
});

// Set the IP address in configuration for Eureka registration
builder.Configuration["eureka:instance:ipAddress"] = localIpAddress;

// Add Eureka Discovery Client for service registration with Eureka server
builder.Services.AddDiscoveryClient(builder.Configuration);

// Add services to the container.
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<WebApiNetContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString)));

// Adding repositories ...
builder.Services.AddScoped<IHouseRepository, HouseRepository>();
builder.Services.AddScoped<IOwnerRepository, OwnerRepository>();

// Adding services ...
builder.Services.AddScoped<IHouseService, HouseService>();
builder.Services.AddScoped<IOwnerService, OwnerService>();

// Adding controllers ...
builder.Services.AddControllers();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (!string.IsNullOrEmpty(pathBase))
{
    app.UsePathBase(pathBase);
}

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

// Map all actuators and controllers directly
app.MapAllActuators(_ => { });
app.MapControllers();

app.UseAuthorization();
app.Run();
