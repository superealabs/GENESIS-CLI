using WebApiNet.Data;
using WebApiNet.Models;
using WebApiNet.Repositories.Generic;
using WebApiNet.Repositories.Interface;

namespace WebApiNet.Repositories.Implementation;

public class HouseRepository : Repository<House, int>, IHouseRepository
{
	public HouseRepository(WebApiNetContext context) : base(context)
	{
	}
}
