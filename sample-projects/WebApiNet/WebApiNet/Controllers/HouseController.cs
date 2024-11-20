namespace WebApiNet.Controllers;

using Microsoft.AspNetCore.Mvc;
using Models;
using Services.Interface;

[ApiController]
[Route("houses")]
public class HouseController  : ControllerBase 
{
	private readonly IHouseService _service;

	public HouseController(IHouseService service)
	{
		_service = service;
	}

	[HttpGet]
	public async Task<ActionResult<IEnumerable<House>>> GetHouses()
	{
		var houses = await _service.GetAllAsync();
		return houses.Any() ? Ok(houses) : NoContent();
	}

	[HttpGet("{id}")]
	public async Task<ActionResult<House>> GetHouse(int id)
	{
		var house = await _service.GetByIdAsync(id);
		return house == null ? NotFound() : Ok(house);
	}

	[HttpPost]
	public async Task<ActionResult<House>> Post(House house)
	{
		await _service.AddAsync(house);
		return CreatedAtAction(nameof(GetHouse), new { id = house.Houseid }, house);
	}

	[HttpPut("{id}")]
	public async Task<ActionResult<House>> PutHouse(int id, House house)
	{
		if (id != house.Houseid)
			return BadRequest();

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.UpdateAsync(house);
		return NoContent();
	}

	[HttpDelete("{id}")]
	public async Task<ActionResult> DeleteHouse(int id)
	{

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.DeleteAsync(id);
		return NoContent();
	}

}
