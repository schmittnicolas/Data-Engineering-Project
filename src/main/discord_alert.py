import discord
from kafka import KafkaConsumer
import asyncio

intents = discord.Intents.default()
intents.messages = True
client = discord.Client(intents=intents)
channel = client.get_channel(1256999844367564962)

@client.event
async def on_ready():
    print(f'Logged in as {client.user}!')
    if channel:
        await channel.send("Hello from Discord bot!")
    asyncio.create_task(kafka_consumer_task())

async def kafka_consumer_task():
    consumer = KafkaConsumer(
        'alerts',
        bootstrap_servers='localhost:9092',
        auto_offset_reset='earliest',
        group_id='discord-group'
    )

    channel = client.get_channel(1256999844367564962)

    while True:
        for message in consumer:
            if channel:
                data = message.value.decode('utf-8').split(', ')
                latitude = data[0].split(': ')[1]
                longitude = data[1].split(': ')[1]
                day_of_week = data[2].split(': ')[1]
                alcohol_level = data[3].split(': ')[1]
                
                embed = discord.Embed(title="🚨 Kafka Alert 🚨", color=0xff0000)
                embed.add_field(name="Latitude", value=latitude, inline=True)
                embed.add_field(name="Longitude", value=longitude, inline=True)
                embed.add_field(name="Day of Week", value=day_of_week, inline=False)
                embed.add_field(name="Alcohol Level", value=f"{alcohol_level} g/L", inline=False)
                await channel.send(embed=embed)


client.run('MTI1NzE1MDIzNTY4ODU3MTAyMg.GFMigN.i4TfLFESxKzBZPJbHCotBBR0otiHTyfDpIt7dY')
