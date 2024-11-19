using WebApiNet.Data;
using WebApiNet.Models;
using WebApiNet.Repositories.Generic;
using WebApiNet.Repositories.Interface;

namespace WebApiNet.Repositories.Implementation;

public class TestingRepository : Repository<Testing, long>, ITestingRepository
{
	public TestingRepository(WebApiNetContext context) : base(context)
	{
	}
}
