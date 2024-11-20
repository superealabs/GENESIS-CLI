namespace WebApiNet.Models;

using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("owners")]
public class Owner  
{
	[Key]
	[DatabaseGenerated(DatabaseGeneratedOption.Identity)]
	[Column("owner_id")]
	public int OwnerId { get; set; }

	[Column("first_name")]
	public string FirstName { get; set; }

	[Column("last_name")]
	public string LastName { get; set; }

	[Column("email")]
	public string Email { get; set; }

	[Column("phone")]
	public string Phone { get; set; }
	

}
