using WebApiNet.Data;
using WebApiNet.Models;
using WebApiNet.Repositories.Generic;
using WebApiNet.Repositories.Interface;

namespace WebApiNet.Repositories.Implementation;

public class OwnerRepository : Repository<Owner, int>, IOwnerRepository
{
	public OwnerRepository(WebApiNetContext context) : base(context)
	{
	}
}
