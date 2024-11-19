namespace WebApiNet.Controllers;

using Microsoft.AspNetCore.Mvc;
using Models;
using Services.Interface;

[ApiController]
[Route("testings")]
public class TestingController  : ControllerBase 
{
	private readonly ITestingService _service;

	public TestingController(ITestingService service)
	{
		_service = service;
	}

	[HttpGet]
	public async Task<ActionResult<IEnumerable<Testing>>> GetTestings()
	{
		var testings = await _service.GetAllAsync();
		return testings.Any() ? Ok(testings) : NoContent();
	}

	[HttpGet("{id}")]
	public async Task<ActionResult<Testing>> GetTesting(long id)
	{
		var testing = await _service.GetByIdAsync(id);
		return testing == null ? NotFound() : Ok(testing);
	}

	[HttpPost]
	public async Task<ActionResult<Testing>> Post(Testing testing)
	{
		await _service.AddAsync(testing);
		return CreatedAtAction(nameof(GetTesting), new { id = testing.Id }, testing);
	}

	[HttpPut("{id}")]
	public async Task<ActionResult<Testing>> PutTesting(long id, Testing testing)
	{
		if (id != testing.Id)
			return BadRequest();

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.UpdateAsync(testing);
		return NoContent();
	}

	[HttpDelete("{id}")]
	public async Task<ActionResult> DeleteTesting(long id)
	{

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.DeleteAsync(id);
		return NoContent();
	}

}
